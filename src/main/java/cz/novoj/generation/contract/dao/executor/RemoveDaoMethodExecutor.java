package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.GenericBucketRepository;
import cz.novoj.generation.contract.dao.executor.dto.DaoMethodQuery;
import cz.novoj.generation.contract.dao.executor.dto.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.executor.visitor.QueryNodeToPredicateVisitor;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.model.traits.PropertyAccessor;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;

/**
 * This implementation will parse method name into {@link DaoMethodQuery}. This object represents query abstract syntax
 * tree with two parts - filter and sort part.
 *
 * This query AST is then translated into {@link Predicate} chain (for filtering collection items in memory).
 * This step can be altered to to transforming query AST into SQL, MongoDB, Elastic Search query or whatever else
 * what comes to your mind.
 *
 * Predicate will then be used to create new collection of the items, that should remain in the repository and they
 * swap original collection in the repository (ie. in atomic operation).
 *
 * Finally this implementation prepares a function that transforms result stream of repository items into required
 * return type of the method.
 *
 * @param <T>
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class RemoveDaoMethodExecutor<T extends PropertyAccessor> extends AbstractDaoMethodExecutor<T> {
	// predicate chain to filter the repository items (it may be empty when no filtering is required)
    private final Optional<Predicate<RepositoryItemWithMethodArgs<T>>> filterPredicate;
	// result function translating stream of repository items into required return type
    private final BiFunction<Stream<T>, Object[], ?> resultTransformer;

	/**
	 * Constructor is called once per method - this object represents method context, that is cached between invocations.
	 * Ie. it can be fairly expensive and it doesn't matter much.
	 *
	 * @param method
	 */
    public RemoveDaoMethodExecutor(Method method) {
		// translate method name to query AST
        final DaoMethodQuery daoMethodQuery = getQueryAST(method.getName());

		// translate query AST filter into predicate chain
        this.filterPredicate = ofNullable(daoMethodQuery.getFilter()).map(this::getFilterPredicate);
		// create result function translating filtered / sorted stream of repository items into the required return type
        this.resultTransformer = getResultTransformer(method);
    }

	/**
	 * Method uses visitor pattern to traverse query AST node tree and convert it to the {@link Predicate} chain.
	 * @param queryNode
	 * @return
	 */
    private Predicate<RepositoryItemWithMethodArgs<T>> getFilterPredicate(QueryNode queryNode) {
        QueryNodeToPredicateVisitor<T> visitor = new QueryNodeToPredicateVisitor<>();
        queryNode.visit(visitor);
        return visitor.getPredicate();
    }

	/**
	 * This method is called on each method invocation and is optimized for speed.
	 * It simply converts one stream to another and finally passes stream to result transforming function.
	 *
	 * @param proxyState
	 * @param args
	 * @return
	 */
    public Object apply(GenericBucketRepository<T> proxyState, Object[] args) {
		// get data stream from our repository
        final Stream<T> mainStream = proxyState.getData().stream();

        // filter data for output if required
        final Stream<T> filteredStream = filterPredicate
				// apply predicate - we need to pass an object wrapping repository item and method invocation arguments
				// if we din't stick to the predicate abstraction we could use BiFunction to avoid mediate object instantiation
                .map(predicate -> mainStream.filter(t -> predicate.test(new RepositoryItemWithMethodArgs<>(t, args))))
				// return original data stream - no filtering was required
				.orElse(mainStream);

        // compute result (at the end data would be already altered)
        final Object result = resultTransformer.apply(filteredStream, args);

        // get data stream from our repository again
        final Stream<T> streamForRemoval = proxyState.getData().stream();

        // filter data that are NOT marked for removal and collect new list
        final List<T> dataThatWillRemain = filterPredicate
                .map(
                		predicate -> streamForRemoval
								// filter stream again but in negative stance - we need to get items that will REMAIN in repository
                                .filter(t -> !predicate.test(new RepositoryItemWithMethodArgs<>(t, args)))
								// convert final stream into collection
                                .collect(toCollection(() -> (List<T>)new LinkedList<T>()))
                )
				// if no filter is defined retain all
				.orElse(proxyState.getData());

        // swap original data collection with new one in one atomic operation
        proxyState.resetDataTo(dataThatWillRemain);

        return result;
    }

}
