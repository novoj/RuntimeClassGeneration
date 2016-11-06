package cz.novoj.generation.contract.dao.keyword.instance;

import cz.novoj.generation.contract.dao.keyword.Keyword;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface KeywordInstance {

    Keyword getKeyword();

    void visit(KeywordInstanceVisitor visitor);

}
