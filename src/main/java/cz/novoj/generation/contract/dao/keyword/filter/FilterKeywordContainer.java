package cz.novoj.generation.contract.dao.keyword.filter;

import cz.novoj.generation.contract.dao.keyword.Keyword;
import cz.novoj.generation.contract.dao.keyword.KeywordContainer;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
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
