package cz.novoj.generation.contract.dao.dto;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
@Data
public class KeywordWithSubKeywords implements KeywordInstance {
    private final KeywordContainer keyword;
    private final List<KeywordInstance> subKeywords = new LinkedList<>();

    public void addSubKeyword(KeywordInstance keywordInstance) {
        this.subKeywords.add(keywordInstance);
    }

    @Override
    public void visit(KeywordInstanceVisitor visitor) {
        visitor.accept(this);
    }
}
