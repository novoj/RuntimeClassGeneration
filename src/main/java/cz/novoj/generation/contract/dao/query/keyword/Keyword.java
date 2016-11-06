package cz.novoj.generation.contract.dao.query.keyword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface Keyword {

    enum Kind { Filter, Sort }
    enum Type { Leaf, Container }

    Map<Kind, Map<String, Keyword>> ALL_KEYWORDS = new HashMap<>(Kind.values().length);

    static void registerKeyword(Keyword keyword) {
        synchronized (ALL_KEYWORDS) {
            Map<String, Keyword> keywordMap = ALL_KEYWORDS.computeIfAbsent(keyword.getKind(), kind -> new HashMap<>(64));
            keywordMap.put(keyword.name(), keyword);
        }
    }

    String name();

    Kind getKind();

    default Type getType() {
        return Type.Leaf;
    }

    static Keyword findKeyword(Kind kind, List<String> keywords) {
        final StringBuilder composite = new StringBuilder();
        Keyword longestKeyword = null;
        for (int i = keywords.size() - 1; i >= 0; i--) {
            final String keywordName = keywords.get(i);
            composite.insert(0, keywordName);
            Optional<Keyword> keyword = ofNullable(ALL_KEYWORDS.get(kind)).map(kMap -> kMap.get(composite.toString()));
            if (keyword.isPresent()) {
                longestKeyword = keyword.get();
            }
        }
        return longestKeyword;
    }

}
