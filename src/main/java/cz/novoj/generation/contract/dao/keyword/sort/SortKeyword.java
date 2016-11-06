package cz.novoj.generation.contract.dao.keyword.sort;

import cz.novoj.generation.contract.dao.keyword.Keyword;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
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
