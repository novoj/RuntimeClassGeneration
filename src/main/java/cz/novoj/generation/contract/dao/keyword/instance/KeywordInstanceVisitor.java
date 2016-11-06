package cz.novoj.generation.contract.dao.keyword.instance;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface KeywordInstanceVisitor {

    void accept(KeywordWithConstant keywordInstance);

    void accept(KeywordWithSubKeywords keywordInstance);

}
