package cz.novoj.generation.contract.dao.query.instance;

import cz.novoj.generation.contract.dao.query.keyword.KeywordContainer;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
@Data
public class ContainerQueryNode implements QueryNode {
    private final KeywordContainer keyword;
    private final List<QueryNode> subKeywords = new LinkedList<>();

    public void addSubKeyword(QueryNode queryNode) {
        this.subKeywords.add(queryNode);
    }

    @Override
    public void visit(QueryNodeVisitor visitor) {
        visitor.accept(this);
    }

}
