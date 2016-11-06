package cz.novoj.generation.contract.dao.keyword.sort;

import cz.novoj.generation.contract.dao.keyword.Keyword;
import cz.novoj.generation.contract.dao.keyword.KeywordContainer;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
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
