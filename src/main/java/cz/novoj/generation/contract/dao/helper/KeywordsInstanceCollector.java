package cz.novoj.generation.contract.dao.helper;

import cz.novoj.generation.contract.dao.dto.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Optional.ofNullable;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
class KeywordsInstanceCollector implements Collector<String, KeywordsInstanceCollector.KeywordInstanceAccumulator, KeywordInstance> {
    private final KeywordWithSubKeywords defaultContainer;

    KeywordsInstanceCollector(KeywordWithSubKeywords defaultContainer) {
        Objects.nonNull(FilterKeywordContainer.values());
        Objects.nonNull(FilterKeyword.values());
        this.defaultContainer = defaultContainer;
    }

    @Override
    public Supplier<KeywordInstanceAccumulator> supplier() {
        return () -> new KeywordInstanceAccumulator(defaultContainer);
    }

    @Override
    public BiConsumer<KeywordInstanceAccumulator, String> accumulator() {
        return (acc, s) -> {
            final Keyword keyword = findKeyword(acc, s);
            if (keyword != null) {
                acc.addKeywordAdept(keyword);
            } else if (!acc.getKeywordAdepts().isEmpty()) {
                registerKeywordInstance(acc, FilterKeyword.Eq, acc.getKeywordAdepts());
                acc.clearKeywordAdepts();
            }
            acc.addConstant(s);
        };
    }

    @Override
    public BinaryOperator<KeywordInstanceAccumulator> combiner() {
        return (k1, k2) -> {
            throw new UnsupportedOperationException("Unsupported.");
        };
    }

    @Override
    public Function<KeywordInstanceAccumulator, KeywordInstance> finisher() {
        return acc -> {
            if (!acc.getConstant().isEmpty()) {
                registerKeywordInstance(acc, FilterKeyword.Eq, acc.getKeywordAdepts());
            }
            return acc.getFinalKeyword();
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    private void registerKeywordInstance(KeywordInstanceAccumulator acc, Keyword defaultKeyword, List<Keyword> keywords) {
        String bareConstant = acc.popConstant(getComposedName(keywords));

        KeywordWithSubKeywords keywordInstanceAffectingNextKeyword = null;
        for (Keyword keyword : keywords) {
            if (keyword instanceof KeywordContainer) {
                if (!((KeywordContainer) keyword).affectsNextKeyword()) {
                    if (bareConstant != null) {
                        acc.addKeywordInstance(new KeywordWithConstant(defaultKeyword, bareConstant, acc.getConstantIndexAndIncrement()));
                        bareConstant = null;
                    }
                    acc.addKeywordInstance(new KeywordWithSubKeywords((KeywordContainer) keyword));
                } else {
                    keywordInstanceAffectingNextKeyword = new KeywordWithSubKeywords((KeywordContainer) keyword);
                }
            } else {
                if (keywordInstanceAffectingNextKeyword == null) {
                    acc.addKeywordInstance(new KeywordWithConstant(keyword, bareConstant, acc.getConstantIndexAndIncrement()));
                } else {
                    keywordInstanceAffectingNextKeyword.addSubKeyword(new KeywordWithConstant(keyword, bareConstant, acc.getConstantIndexAndIncrement()));
                    acc.addKeywordInstance(keywordInstanceAffectingNextKeyword);
                }
                bareConstant = null;
            }
        }

        if (bareConstant != null) {
            acc.addKeywordInstance(new KeywordWithConstant(defaultKeyword, bareConstant, acc.getConstantIndexAndIncrement()));
        }
    }


    private Keyword findKeyword(KeywordInstanceAccumulator acc, String s) {
        final List<String> constant = new ArrayList<>(acc.getConstant());
        constant.add(s);
        return Keyword.findKeyword(constant);
    }

    private static String getComposedName(KeywordWithSubKeywords keywords) {
        final StringBuilder composedName = new StringBuilder(keywords.getKeyword().name());
        for (KeywordInstance keyword : keywords.getSubKeywords()) {
            composedName.append(keyword.getKeyword().name());
        }
        return composedName.toString();
    }

    private static String getComposedName(List<Keyword> keywords) {
        final StringBuilder composedName = new StringBuilder();
        for (Keyword keyword : keywords) {
            ofNullable(keyword).ifPresent(k -> composedName.append(k.name()));
        }
        return composedName.toString();
    }


    @Data
    static class KeywordInstanceAccumulator {
        private KeywordWithSubKeywords rootKeyword;
        private LinkedList<String> constant = new LinkedList<>();
        private LinkedList<Keyword> keywordAdepts = new LinkedList<>();
        @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE) private int constantIndex;

        KeywordInstanceAccumulator(KeywordWithSubKeywords defaultContainer) {
            this.rootKeyword = defaultContainer;
        }

        int getConstantIndexAndIncrement() {
            return constantIndex++;
        }

        void addKeywordAdept(Keyword keyword) {
            if (keywordAdepts.isEmpty() || !keywordAdepts.getLast().getType().equals(keyword.getType())) {
                keywordAdepts.add(keyword);
            } else {
                keywordAdepts.set(keywordAdepts.size() - 1, keyword);
            }
        }

        void clear(String constantPart) {
            final Iterator<String> it = constant.descendingIterator();
            int counter = constantPart.length();
            while (it.hasNext()) {
                final String constant = it.next();
                if (counter > 0) {
                    counter = counter - constant.length();
                    it.remove();
                }
                if (counter <= 0) {
                    break;
                }
            }
        }

        void clearKeywordAdepts() {
            this.keywordAdepts.clear();
        }

        void addConstant(String constant) {
            this.constant.add(constant);
        }

        void addKeywordInstance(KeywordWithSubKeywords keywordInstance) {
            clear(getComposedName(keywordInstance));

            if (keywordInstance.getKeyword().affectsNextKeyword()) {
                this.rootKeyword.addSubKeyword(keywordInstance);
            } else {
                if (this.rootKeyword.getKeyword() == keywordInstance.getKeyword()) {
                    // do nothing - we have proper container at hand already
                } else if (this.rootKeyword.getSubKeywords().size() == 1) {
                    keywordInstance.addSubKeyword(this.rootKeyword.getSubKeywords().get(0));
                    this.rootKeyword = keywordInstance;
                } else {
                    keywordInstance.addSubKeyword(this.rootKeyword);
                    this.rootKeyword = keywordInstance;
                }
            }
        }

        void addKeywordInstance(KeywordWithConstant keywordInstance) {
            clear(keywordInstance.getConstant() + keywordInstance.getKeyword());
            this.rootKeyword.addSubKeyword(keywordInstance);
        }

        String popConstant() {
            final String constant = StringUtils.join(this.constant, null);
            return StringUtils.uncapitalize(constant);
        }

        String popConstant(String keywordName) {
            final String constant = popConstant();
            return StringUtils.uncapitalize(
                    constant.substring(0, constant.length() - keywordName.length())
            );
        }

        public KeywordInstance getFinalKeyword() {
            if (this.rootKeyword.getSubKeywords().isEmpty()) {
                return null;
            } else if (this.rootKeyword.getSubKeywords().size() == 1) {
                return this.rootKeyword.getSubKeywords().get(0);
            } else {
                return this.rootKeyword;
            }
        }

    }

}
