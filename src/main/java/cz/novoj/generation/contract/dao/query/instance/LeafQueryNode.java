package cz.novoj.generation.contract.dao.query.instance;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import lombok.Data;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
@Data
public class LeafQueryNode implements QueryNode {
    private final Keyword keyword;
    private final String constant;
    private final Integer index;

    @Override
    public void visit(QueryNodeVisitor visitor) {
        visitor.accept(this);
    }
}
