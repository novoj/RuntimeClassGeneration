package cz.novoj.generation.contract.dao.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface Keyword {

    Map<String, Keyword> ALL_KEYWORDS = new HashMap<>(64);

    static void registerKeyword(Keyword keyword) {
        ALL_KEYWORDS.put(keyword.name(), keyword);
    }

    String name();

    default Class getType() {
        return Keyword.class;
    }

    static Keyword findKeyword(List<String> keywords) {
        final StringBuilder composite = new StringBuilder();
        Keyword longestKeyword = null;
        for (int i = keywords.size() - 1; i >= 0; i--) {
            final String keywordName = keywords.get(i);
            composite.insert(0, keywordName);
            Keyword keyword = ALL_KEYWORDS.get(composite.toString());
            if (keyword != null) {
                longestKeyword = keyword;
            }
        }
        return longestKeyword;
    }

}
