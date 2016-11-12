package cz.novoj.generation.contract.dao.query.keyword;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Keyword recognized by the query AST.
 */
public interface Keyword {

	/**
	 * Purpose of the keyword.
	 */
    enum Purpose { Filter, Sort }

	/**
	 * Type of the keyword - ie. whether query node connected to this keyword can have child nodes or not.
	 */
	enum Type { Leaf, Container }

	/**
	 * Hack so that we can iterate over all existing keywords.
	 */
    Map<Purpose, Map<String, Keyword>> ALL_KEYWORDS = new EnumMap<>(Purpose.class);

	/**
	 * Name of the keyword - such as equals, lessThan, and etc.
	 * @return
	 */
	String name();

	/**
	 * Purpose of the keyword - ie. whether it is aimed for filtering or sorting.
	 * @return
	 */
    Purpose getPurpose();

	/**
	 * Type of the keyword - leaf or container.
	 * @return
	 */
	default Type getType() {
        return Type.Leaf;
    }

	/**
	 * Method that is called by each keyword upon instantiation.
	 * @param keyword
	 */
	static void registerKeyword(Keyword keyword) {
		synchronized (ALL_KEYWORDS) {
			Map<String, Keyword> keywordMap = ALL_KEYWORDS.computeIfAbsent(keyword.getPurpose(), kind -> new HashMap<>(64));
			keywordMap.put(keyword.name(), keyword);
		}
	}

	/**
	 * Finds keyword that consists of maximum count of words passed in argument
	 * @param purpose
	 * @param words
	 * @return
	 */
    static Keyword findKeyword(Purpose purpose, List<String> words) {
        final StringBuilder composite = new StringBuilder(128);
        Keyword longestKeyword = null;
        for (int i = words.size() - 1; i >= 0; i--) {
            final String keywordName = words.get(i);
            composite.insert(0, keywordName);
            Optional<Keyword> keyword = ofNullable(ALL_KEYWORDS.get(purpose)).map(kMap -> kMap.get(composite.toString()));
            if (keyword.isPresent()) {
                longestKeyword = keyword.get();
            }
        }
        return longestKeyword;
    }

}
