package cz.novoj.generation.contract.dao.query.instance;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface QueryNode {

    Keyword getKeyword();

    void visit(QueryNodeVisitor visitor);

}
