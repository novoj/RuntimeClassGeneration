package cz.novoj.generation.contract.dao.keyword.instance;

import cz.novoj.generation.contract.dao.keyword.Keyword;
import lombok.Data;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
@Data
public class KeywordWithConstant implements KeywordInstance {
    private final Keyword keyword;
    private final String constant;
    private final Integer index;

    @Override
    public void visit(KeywordInstanceVisitor visitor) {
        visitor.accept(this);
    }
}
