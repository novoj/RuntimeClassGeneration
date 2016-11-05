package cz.novoj.generation.contract.dao.dto;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface KeywordInstance {

    Keyword getKeyword();

    void visit(KeywordInstanceVisitor visitor);

}
