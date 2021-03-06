package cz.novoj.generation.contract.dao.executor.visitor;

import cz.novoj.generation.contract.dao.executor.dto.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.query.instance.ContainerQueryNode;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNodeVisitor;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This visitor translates query AST node tree into the {@link Predicate} chain.
 * Other implementation might generate SQL, MongoDB, Elastic Search query.
 *
 * @param <U>
 */
public class QueryNodeToPredicateVisitor<U extends PropertyAccessor> implements QueryNodeVisitor {
	// stack is used for composing predicate chain
	private final Stack<Consumer<Predicate<RepositoryItemWithMethodArgs<U>>>> predicateConsumer = new Stack<>();
	// final predicate created by this visitor
	@Getter private Predicate<RepositoryItemWithMethodArgs<U>> predicate;

    public QueryNodeToPredicateVisitor() {
        // initially what's passed to the consumer is set to the root predicate (ie. result)
        predicateConsumer.push(p -> this.predicate = p);
    }

	/**
	 * Visits {@link LeafQueryNode} and converts it into {@link Predicate}.
	 *
	 * @param queryNode
	 */
	@Override
    public void accept(LeafQueryNode queryNode) {
        predicateConsumer.peek()
						 .accept(PredicateFactory.createPredicate(queryNode));
    }

	/**
	 * Visits {@link ContainerQueryNode} and propagates visit to all container children.
	 *
	 * @param queryNode
	 */
	@Override
    public void accept(ContainerQueryNode queryNode) {
		/**
		 * create new query node consumer that chains all predicates inside this query node container into one
		 * ie. when this query container is AND all children will be chained by {@link Predicate#and(Predicate)} method.
		 **/
        final ContainerPredicateConsumer<U> subKeywordPredicateConsumer =
                new ContainerPredicateConsumer<>((FilterKeywordContainer) queryNode.getKeyword());

        // register our consumer on the stack, so that all children use it for composition
        predicateConsumer.push(subKeywordPredicateConsumer);
        // visit all children
        for (QueryNode ki : queryNode.getSubKeywords()) {
            ki.visit(this);
        }
        // remove consumer from the stack - we're returning one level up from the tree
        predicateConsumer.pop();
		// consume result predicate by the previous consumer
        predicateConsumer.peek().accept(subKeywordPredicateConsumer.getFinalPredicate());
    }

	/**
	 * This consumer class is used to combine (chain) multiple {@link Predicate} into one based on the query node keyword.
	 * @param <U>
	 */
    @RequiredArgsConstructor
    private static class ContainerPredicateConsumer<U extends PropertyAccessor> implements Consumer<Predicate<RepositoryItemWithMethodArgs<U>>> {
		/**
		 * this keyword determines what type of chaining should occur -
		 * ie. {@link Predicate#and(Predicate)},  {@link Predicate#or(Predicate)}
		 **/
        private final FilterKeywordContainer keyword;
        // chained predicates are stored in this field
        @Getter private Predicate<RepositoryItemWithMethodArgs<U>> finalPredicate;

        @Override
        public void accept(Predicate<RepositoryItemWithMethodArgs<U>> t) {
            finalPredicate = PredicateFactory.createPredicate(keyword, finalPredicate, t);
        }
    }
}
