package cz.novoj.generation.contract.dao.executor.collector;

import cz.novoj.generation.contract.dao.executor.dto.DaoMethodQuery;
import cz.novoj.generation.contract.dao.executor.dto.QueryNodeAccumulator;
import cz.novoj.generation.contract.dao.executor.dto.QueryAccumulator;
import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import cz.novoj.generation.contract.dao.query.keyword.Keyword.Purpose;
import cz.novoj.generation.contract.dao.query.keyword.KeywordContainer;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeywordContainer;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Optional.ofNullable;

/**
 * State maching parsing method name into the query AST.
 * This is adept for replacement with some ANTLR implementation or some other more powerful implementation.
 *
 * I'm not proud of this implementation much :)
 *
 */
public class QueryCollector implements Collector<String, QueryAccumulator, DaoMethodQuery> {
    private final AbstractMap<Purpose, String> kindPrefixes = new EnumMap<>(Purpose.class);
    private final FilterKeywordContainer defaultFilterContainer;
    private final FilterKeyword defaultFilterKeyword;
    private final SortKeywordContainer defaultSortContainer;
    private final SortKeyword defaultSortKeyword;

    public QueryCollector(FilterKeywordContainer defaultFilterContainer, FilterKeyword defaultFilterKeyword,
						  SortKeywordContainer defaultSortContainer, SortKeyword defaultSortKeyword) {
        Objects.nonNull(FilterKeywordContainer.values());
        Objects.nonNull(FilterKeyword.values());
        Objects.nonNull(SortKeyword.values());
        Objects.nonNull(SortKeywordContainer.values());
        this.defaultFilterContainer = defaultFilterContainer;
        this.defaultFilterKeyword = defaultFilterKeyword;
        this.defaultSortContainer = defaultSortContainer;
        this.defaultSortKeyword = defaultSortKeyword;
        kindPrefixes.put(Purpose.Filter, "By");
        kindPrefixes.put(Purpose.Sort, "SortedBy");
    }

    @Override
    public Supplier<QueryAccumulator> supplier() {
        return () -> new QueryAccumulator(
                new QueryNodeAccumulator(defaultFilterContainer, defaultFilterKeyword),
                new QueryNodeAccumulator(defaultSortContainer, defaultSortKeyword)
        );
    }

    @Override
    public BiConsumer<QueryAccumulator, String> accumulator() {
        return (acc, s) -> {
            final Optional<QueryNodeAccumulator> activeAcc = ofNullable(acc.getActiveAccumulator());
            final List<String> accumulatedWords = activeAcc.map(QueryNodeAccumulator::getWords).orElse(acc.getUnrecognizedWords());
            final List<Keyword> keywordAdepts = activeAcc.map(QueryNodeAccumulator::getKeywordAdepts).orElse(Collections.emptyList());

            final Keyword keyword = activeAcc.map(kiAcc -> findKeyword(s, accumulatedWords, kiAcc.getPurpose())).orElse(null);
            final Purpose purposePrefix = findKindPrefix(s, accumulatedWords);

            if (purposePrefix != null) {
                activeAcc.ifPresent(kia -> {
                    kia.addWord(s);
                    kia.clear(kindPrefixes.get(purposePrefix));
                    if (!accumulatedWords.isEmpty()) {
                        registerKeywordInstance(kia, keywordAdepts);
                    }
                });
                acc.switchAccumulator(purposePrefix);
                return;
            } else if (keyword != null) {
                activeAcc.ifPresent(kia -> kia.addKeywordAdept(keyword));
            } else if (!keywordAdepts.isEmpty()) {
                activeAcc.ifPresent(kia -> registerKeywordInstance(kia, keywordAdepts));
            }

            accumulatedWords.add(s);
        };
    }

    @Override
    public BinaryOperator<QueryAccumulator> combiner() {
        return (k1, k2) -> {
            throw new UnsupportedOperationException("Unsupported.");
        };
    }

    @Override
    public Function<QueryAccumulator, DaoMethodQuery> finisher() {
        return acc -> {
            Optional<QueryNodeAccumulator> activeAcc = ofNullable(acc.getActiveAccumulator());
            activeAcc.ifPresent(aacc -> {
                if (!aacc.getWords().isEmpty()) {
                    registerKeywordInstance(aacc, aacc.getKeywordAdepts());
                }
            });

            return new DaoMethodQuery(
                acc.getAccumulators()
            );
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    private void registerKeywordInstance(QueryNodeAccumulator acc, List<Keyword> keywords) {
        String bareConstant = acc.popConstant(getComposedName(keywords));

        KeywordContainer keywordAffectingNextKeyword = null;
        for (Keyword keyword : keywords) {
            if (keyword instanceof KeywordContainer) {
                if (keyword instanceof FilterKeywordContainer && !((FilterKeywordContainer) keyword).affectsNextKeyword()) {
                    if (bareConstant != null) {
                        acc.addKeywordInstance(bareConstant);
                        bareConstant = null;
                    }
                    acc.addKeywordContainerInstance((KeywordContainer) keyword);
                } else {
                    keywordAffectingNextKeyword = (KeywordContainer) keyword;
                }
            } else {
                if (keywordAffectingNextKeyword == null) {
                    acc.addKeywordInstance(keyword, bareConstant);
                } else {
                    acc.addKeywordContainerInstanceWithChild(keywordAffectingNextKeyword, keyword, bareConstant);
                }
                bareConstant = null;
            }
        }

        if (bareConstant != null) {
            acc.addKeywordInstance(bareConstant);
        }

        acc.clearKeywordAdepts();
    }

    private Purpose findKindPrefix(String currentWord, List<String> words) {
        final StringBuilder sb = new StringBuilder(128);
        words.forEach(sb::append);
        sb.append(currentWord);
        final String composedWord = sb.toString();
        Purpose result = null;
        String resultPrefix = null;
        for (Entry<Purpose, String> entry : kindPrefixes.entrySet()) {
            if (composedWord.endsWith(entry.getValue())) {
                if (result == null || resultPrefix.length() < entry.getValue().length()) {
                    result = entry.getKey();
                    resultPrefix = entry.getValue();
                }
            }
        }
        return result;
    }

    private Keyword findKeyword(String currentWord, List<String> words, Purpose purpose) {
        final List<String> constant = new ArrayList<>(words);
        constant.add(currentWord);
        return Keyword.findKeyword(purpose, constant);
    }

    private static String getComposedName(List<Keyword> keywords) {
        final StringBuilder composedName = new StringBuilder(128);
        for (Keyword keyword : keywords) {
            ofNullable(keyword).ifPresent(k -> composedName.append(k.name()));
        }
        return composedName.toString();
    }

}
