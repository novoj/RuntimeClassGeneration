package cz.novoj.generation.contract.dao.keyword;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface KeywordContainer extends Keyword {

    @Override
    default Type getType() {
        return Type.Container;
    }

}
