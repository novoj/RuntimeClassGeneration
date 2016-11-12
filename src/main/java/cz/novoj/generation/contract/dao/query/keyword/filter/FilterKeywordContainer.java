package cz.novoj.generation.contract.dao.query.keyword.filter;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import cz.novoj.generation.contract.dao.query.keyword.KeywordContainer;


public enum FilterKeywordContainer implements KeywordContainer {

    And(false),
    Or(false),
    Not(true);

    private final boolean affectsNextKeyword;

    FilterKeywordContainer(boolean affectsNextKeyword) {
        Keyword.registerKeyword(this);
        this.affectsNextKeyword = affectsNextKeyword;
    }

    @Override
    public Kind getKind() {
        return Kind.Filter;
    }

    public boolean affectsNextKeyword() {
        return affectsNextKeyword;
    }
}
