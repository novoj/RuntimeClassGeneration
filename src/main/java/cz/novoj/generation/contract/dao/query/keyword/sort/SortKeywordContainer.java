package cz.novoj.generation.contract.dao.query.keyword.sort;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import cz.novoj.generation.contract.dao.query.keyword.KeywordContainer;


public enum SortKeywordContainer implements KeywordContainer {

    And;

    SortKeywordContainer() {
        Keyword.registerKeyword(this);
    }

    @Override
    public Kind getKind() {
        return Kind.Sort;
    }

}
