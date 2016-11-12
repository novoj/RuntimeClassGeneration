package cz.novoj.generation.contract.dao.query.instance;

import cz.novoj.generation.contract.dao.query.keyword.Keyword;

/**
 * Query node represents single item in the query AST.
 */
public interface QueryNode {

	/**
	 * Returns keyword this node refers to.
	 * @return
	 */
    Keyword getKeyword();

	/**
	 * Method allows traversing query AST by visitor pattern.
	 * @param visitor
	 */
	void visit(QueryNodeVisitor visitor);

}
