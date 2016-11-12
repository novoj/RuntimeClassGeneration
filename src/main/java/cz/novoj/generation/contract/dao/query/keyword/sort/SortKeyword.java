package cz.novoj.generation.contract.dao.query.keyword.sort;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;

/**
 * Sorting leaf keywords.
 */
public enum SortKeyword implements Keyword {

    Asc,
    Desc;

    SortKeyword() {
        Keyword.registerKeyword(this);
    }

    @Override
    public Purpose getPurpose() {
        return Purpose.Sort;
    }

}
