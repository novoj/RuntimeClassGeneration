package cz.novoj.generation.contract.dao.query.keyword.sort;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;


public enum SortKeyword implements Keyword {

    Asc,
    Desc;

    SortKeyword() {
        Keyword.registerKeyword(this);
    }

    @Override
    public Kind getKind() {
        return Kind.Sort;
    }

}
