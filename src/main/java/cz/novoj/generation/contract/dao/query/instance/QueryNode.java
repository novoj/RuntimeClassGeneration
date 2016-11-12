package cz.novoj.generation.contract.dao.query.instance;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;


public interface QueryNode {

    Keyword getKeyword();

    void visit(QueryNodeVisitor visitor);

}
