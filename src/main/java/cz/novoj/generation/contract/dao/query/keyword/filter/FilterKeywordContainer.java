package cz.novoj.generation.contract.dao.query.keyword.filter;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import cz.novoj.generation.contract.dao.query.keyword.KeywordContainer;
import lombok.Getter;

/**
 * Filter container keywords.
 */
public enum FilterKeywordContainer implements KeywordContainer {

    And(false),
    Or(false),
    Not(true);

    // required for proper parsing keywords into the tree
    @Getter private final boolean affectsNextKeyword;

    FilterKeywordContainer(boolean affectsNextKeyword) {
        Keyword.registerKeyword(this);
        this.affectsNextKeyword = affectsNextKeyword;
    }

    @Override
    public Purpose getPurpose() {
        return Purpose.Filter;
    }

}
