package cz.novoj.generation.contract.dao.query.keyword.filter;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import lombok.Getter;

/**
 * Leaf filtering keywords.
 */
public enum FilterKeyword implements Keyword {

    Eq(true),
    LessThan(true),
    LessThanEq(true),
    MoreThan(true),
    MoreThanEq(true),
    Contains(true),
    IsNull(false),
    IsNotNull(false),
    In(true);

    // returns true is it needs to be accompanied by argument in method signature
    @Getter final boolean requiresArgument;

    FilterKeyword(boolean requiresArgument) {
        Keyword.registerKeyword(this);
        this.requiresArgument = requiresArgument;
    }

    @Override
    public Purpose getPurpose() {
        return Purpose.Filter;
    }

}
