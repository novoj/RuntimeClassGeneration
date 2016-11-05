package cz.novoj.generation.contract.dao.dto;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface KeywordContainer extends Keyword {

    boolean affectsNextKeyword();

    @Override
    default Class getType() {
        return KeywordContainer.class;
    }

}
