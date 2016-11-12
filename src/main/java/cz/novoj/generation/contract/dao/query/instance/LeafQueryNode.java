package cz.novoj.generation.contract.dao.query.instance;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import lombok.Data;

/**
 * Leaf query node that can't have another query nodes inside.
 */
@Data
public class LeafQueryNode implements QueryNode {
    private final Keyword keyword;
    private final String propertyName;
    private final Integer argIndex;

    @Override
    public void visit(QueryNodeVisitor visitor) {
        visitor.accept(this);
    }
}
