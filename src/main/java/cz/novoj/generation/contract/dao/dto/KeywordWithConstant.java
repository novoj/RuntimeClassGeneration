package cz.novoj.generation.contract.dao.dto;

import lombok.Data;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
@Data
public class KeywordWithConstant implements KeywordInstance {
    private final Keyword keyword;
    private final String constant;
    private final int index;

    @Override
    public void visit(KeywordInstanceVisitor visitor) {
        visitor.accept(this);
    }
}
