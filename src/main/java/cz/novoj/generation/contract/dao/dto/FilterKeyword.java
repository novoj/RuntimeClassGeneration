package cz.novoj.generation.contract.dao.dto;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public enum FilterKeyword implements Keyword {

    Eq, LessThan, LessThanEq, MoreThan, MoreThanEq, Contains, IsNull, In;

    FilterKeyword() {
        Keyword.registerKeyword(this);
    }

}
