package cz.novoj.generation.contract.dao.query.instance;

/**
 * Interface for the query AST visitors.
 */
public interface QueryNodeVisitor {

	/**
	 * Method will be called on all leaf query nodes of the AST.
	 * @param queryNode
	 */
    void accept(LeafQueryNode queryNode);

	/**
	 * Method will be called on all container nodes of the AST (ie. nodes with children).
	 * @param queryNode
	 */
	void accept(ContainerQueryNode queryNode);

}
