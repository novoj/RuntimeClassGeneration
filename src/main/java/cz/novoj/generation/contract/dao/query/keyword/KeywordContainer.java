package cz.novoj.generation.contract.dao.query.keyword;

/**
 * Container type of the keyword like AND/OR/NOT.
 */
public interface KeywordContainer extends Keyword {

    @Override
    default Type getType() {
        return Type.Container;
    }

}
