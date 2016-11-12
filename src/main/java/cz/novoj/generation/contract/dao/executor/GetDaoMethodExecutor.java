package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.GenericBucketRepository;
import cz.novoj.generation.contract.dao.executor.dto.DaoMethodQuery;
import cz.novoj.generation.contract.dao.executor.dto.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.executor.visitor.QueryNodeToComparatorVisitor;
import cz.novoj.generation.contract.dao.executor.visitor.QueryNodeToPredicateVisitor;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.model.traits.PropertyAccessor;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * This implementation will parse method name into {@link DaoMethodQuery}. This object represents query abstract syntax
 * tree with two parts - filter and sort part.
 *
 * This query AST is then translated into {@link Predicate} chain (for filtering collection items in memory) and
 * {@link Comparator} chain (for sorting collection items in memory). This step can be altered to to transforming
 * query AST into SQL, MongoDB, Elastic Search query or whatever else what comes to your mind.
 *
 * Finally this implementation prepares a function that transforms result stream of repository items into required
 * return type of the method.
 *
 * @param <T>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GetDaoMethodExecutor<T extends PropertyAccessor> extends AbstractDaoMethodExecutor<T> {
	// predicate chain to filter the repository items (it may be empty when no filtering is required)
    private final Optional<Predicate<RepositoryItemWithMethodArgs<T>>> filterPredicate;
    // comparator chain to filter the repository items (it may be empty when no sorting is required)
    private final Optional<Comparator<T>> orderComparator;
    // result function translating stream of repository items into required return type
    private final BiFunction<Stream<T>, Object[], ?> resultTransformer;

	/**
	 * Constructor is called once per method - this object represents method context, that is cached between invocations.
	 * Ie. it can be fairly expensive and it doesn't matter much.
	 *
	 * @param method
	 */
    public GetDaoMethodExecutor(Method method) {
    	// translate method name to query AST
        final DaoMethodQuery queryAST = getQueryAST(method.getName());

        // translate query AST filter into predicate chain
        this.filterPredicate = ofNullable(queryAST.getFilter()).map(queryNode -> getFilterPredicate(method, queryNode));
		// translate query AST sort into comparator chain
        this.orderComparator = ofNullable(queryAST.getSort()).map(this::getSortComparator);
		// create result function translating filtered / sorted stream of repository items into the required return type
        this.resultTransformer = getResultTransformer(method);
    }

	/**
	 * Method uses visitor pattern to traverse query AST node tree and convert it to the {@link Predicate} chain.
	 * @param method
	 * @param queryNode
	 * @return
	 */
	private Predicate<RepositoryItemWithMethodArgs<T>> getFilterPredicate(Method method, QueryNode queryNode) {
		QueryNodeToPredicateVisitor<T> visitor = new QueryNodeToPredicateVisitor<>(method);
		queryNode.visit(visitor);
		return visitor.getPredicate();
	}

	/**
	 * Method uses visitor pattern to traverse query AST node tree and convert it to the {@link Comparator} chain.
	 * @param queryNode
	 * @return
	 */
	private Comparator<T> getSortComparator(QueryNode queryNode) {
        QueryNodeToComparatorVisitor<T> visitor = new QueryNodeToComparatorVisitor<>();
        queryNode.visit(visitor);
        return visitor.getComparator();
    }

	/**
	 * This method is called on each method invocation and is optimized for speed.
	 * It simply converts one stream to another and finally passes stream to result transforming function.
	 *
	 * @param proxyState
	 * @param args
	 * @return
	 */
    @Override
    public Object apply(GenericBucketRepository<T> proxyState, Object... args) {
    	// get data stream from our repository
        final Stream<T> mainStream = proxyState.getData().stream();

        // filter data if required
        final Stream<T> filteredStream = filterPredicate
				// apply predicate - we need to pass an object wrapping repository item and method invocation arguments
				// if we din't stick to the predicate abstraction we could use BiFunction to avoid mediate object instantiation
                .map(predicate -> mainStream.filter(t -> predicate.test(new RepositoryItemWithMethodArgs<>(t, args))))
				// return original data stream - no filtering was required
                .orElse(mainStream);

        // sort output if required
        final Stream<T> sortedStream = orderComparator
				// apply comparators
                .map(filteredStream::sorted)
				// return original data stream - no ordering was required
                .orElse(filteredStream);

        // return required result type
        return resultTransformer.apply(sortedStream, args);
    }

}
