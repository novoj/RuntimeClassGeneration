package cz.novoj.generation.contract.dao.keyword.filter;

import cz.novoj.generation.contract.dao.keyword.Keyword;
import lombok.Getter;

/**
 * Created by Rodina Novotnych on 05.11.2016.
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

    @Getter
    final boolean requiresArgument;

    FilterKeyword(boolean requiresArgument) {
        Keyword.registerKeyword(this);
        this.requiresArgument = requiresArgument;
    }

    @Override
    public Kind getKind() {
        return Kind.Filter;
    }

}
