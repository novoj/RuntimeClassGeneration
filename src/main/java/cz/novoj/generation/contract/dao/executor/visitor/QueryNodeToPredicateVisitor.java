package cz.novoj.generation.contract.dao.executor.visitor;

import cz.novoj.generation.contract.dao.executor.dto.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.query.instance.ContainerQueryNode;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNodeVisitor;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Optional;
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
    // method used only for nice error messages
	private final Method method;
	// stack is used for composing predicate chain
	private final Stack<Consumer<Predicate<RepositoryItemWithMethodArgs<U>>>> predicateConsumer = new Stack<>();
	// final predicate created by this visitor
	@Getter private Predicate<RepositoryItemWithMethodArgs<U>> predicate;

    public QueryNodeToPredicateVisitor(Method method) {
        this.method = method;
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
						 .accept(riwma -> getPredicate(queryNode, riwma.getRepositoryItem(), riwma.getArgs()));
    }

	/**
	 * Creates predicate for passed query node, repository item and method invocation arguments.
	 * @param queryNode
	 * @param repositoryItem
	 * @param args
	 * @return
	 */
	private boolean getPredicate(LeafQueryNode queryNode, U repositoryItem, Object[] args) {
		final FilterKeyword keyword = (FilterKeyword)queryNode.getKeyword();
		final String propertyName = queryNode.getConstant();
		final Object argument = Optional.ofNullable(queryNode.getIndex())
										.map(argIndex -> getArgumentFromIndex(propertyName, argIndex, args))
										.orElse(null);

		return PropertyAccessor.matches(keyword, repositoryItem, propertyName, argument);
	}

	/**
	 * Retrieves argument from method invocation arguments on specific position.
	 * @param propertyName
	 * @param argIndex
	 * @param args
	 * @return
	 */
	private Object getArgumentFromIndex(String propertyName, Integer argIndex, Object[] args) {

		if (argIndex >= args.length) {
			throw new IllegalArgumentException(
					"No argument in method " + method.toGenericString() +
							" for " + propertyName + " filtering constraint!"
			);
		}

		return args[argIndex];
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
            finalPredicate = PropertyAccessor.matches(keyword, finalPredicate, t);
        }
    }
}
