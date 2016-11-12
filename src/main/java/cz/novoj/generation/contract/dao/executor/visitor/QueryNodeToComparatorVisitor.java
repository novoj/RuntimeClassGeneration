package cz.novoj.generation.contract.dao.executor.visitor;

import cz.novoj.generation.contract.dao.query.instance.ContainerQueryNode;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNodeVisitor;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeywordContainer;
import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This visitor translates query AST node tree into the {@link Comparator} chain.
 * Other implementation might generate SQL, MongoDB, Elastic Search query.
 *
 * @param <U>
 */
public class QueryNodeToComparatorVisitor<U extends PropertyAccessor> implements QueryNodeVisitor {
	// stack is used for composing comparator chain
	private final Stack<Consumer<Comparator<U>>> comparatorConsumer = new Stack<>();
	// final comparator created by this visitor
	@Getter private Comparator<U> comparator;

    public QueryNodeToComparatorVisitor() {
		// initially what's passed to the consumer is set to the root comparator (ie. result)
        comparatorConsumer.push(p -> this.comparator = p);
    }

	/**
	 * Visits {@link LeafQueryNode} and converts it into {@link Comparator}.
	 *
	 * @param queryNode
	 */
    @Override
    public void accept(LeafQueryNode queryNode) {
		// create comparator that will compare o1 and o2
		// according to keyword and property value under specified name
		final SortKeyword keyword = (SortKeyword)queryNode.getKeyword();
		final String propertyName = queryNode.getConstant();
		comparatorConsumer.peek()
						  .accept(ComparatorFactory.compare(keyword, propertyName));
    }

	/**
	 * Visits {@link ContainerQueryNode} and propagates visit to all container children.
	 *
	 * @param queryNode
	 */
    @Override
    public void accept(ContainerQueryNode queryNode) {
		/**
		 * create new query node consumer that chains all comparators inside this query node container into one
		 * ie. when this query container is AND all children will be chained by {@link Comparator#thenComparing(Comparator)} method.
		 **/
        final ContainerComparatorConsumer<U> subKeywordPredicateConsumer =
                new ContainerComparatorConsumer<>((SortKeywordContainer) queryNode.getKeyword());

		// register our consumer on the stack, so that all children use it for composition
        comparatorConsumer.push(subKeywordPredicateConsumer);
		// visit all children
        for (QueryNode ki : queryNode.getSubKeywords()) {
            ki.visit(this);
        }
		// remove consumer from the stack - we're returning one level up from the tree
        comparatorConsumer.pop();
		// consume result predicate by the previous consumer
        comparatorConsumer.peek().accept(subKeywordPredicateConsumer.getFinalComparator());

    }

	/**
	 * This consumer class is used to combine (chain) multiple {@link Predicate} into one based on the query node keyword.
	 * @param <U>
	 */
    @RequiredArgsConstructor
    private static class ContainerComparatorConsumer<U extends PropertyAccessor> implements Consumer<Comparator<U>> {
		/**
		 * this keyword determines what type of chaining should occur - maybe too abstract, because we can use
		 * only {@link Comparator#thenComparing(Comparator)} here
		 **/
        private final SortKeywordContainer keyword;
		// chained comparators are stored in this field
        @Getter private Comparator<U> finalComparator;

        @Override
        public void accept(Comparator<U> t) {
            finalComparator = ComparatorFactory.compare(keyword, finalComparator, t);
        }
    }
}
