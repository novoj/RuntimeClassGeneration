package cz.novoj.generation.contract.dao.query.keyword;


public interface KeywordContainer extends Keyword {

    @Override
    default Type getType() {
        return Type.Container;
    }

}
