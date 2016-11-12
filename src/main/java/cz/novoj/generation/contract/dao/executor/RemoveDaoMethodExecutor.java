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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public class RemoveDaoMethodExecutor<T extends PropertyAccessor> extends AbstractDaoMethodExecutor<T> {

    private final Optional<Predicate<RepositoryItemWithMethodArgs<T>>> filterPredicate;
    private final BiFunction<Stream<T>, Object[], ?> resultTransformer;

    public RemoveDaoMethodExecutor(Method method) {
        final DaoMethodQuery daoMethodQuery = getKeywordInstances(method.getName());

        this.filterPredicate = ofNullable(daoMethodQuery.getFilter()).map(keywordInstance -> getFilterPredicate(method, keywordInstance));
        this.resultTransformer = getResultTransformer(method);
    }

    private Predicate<RepositoryItemWithMethodArgs<T>> getFilterPredicate(Method method, QueryNode queryNode) {
        QueryNodeToPredicateVisitor<T> visitor = new QueryNodeToPredicateVisitor<>(method);
        queryNode.visit(visitor);
        return visitor.getPredicate();
    }

    public Object apply(GenericBucketRepository<T> proxyState, Object[] args) {
        final Stream<T> mainStream = proxyState
                .getData()
                .stream();

        // filter data for output if required
        final Stream<T> filteredStream = filterPredicate
                .map(predicate ->
                        mainStream
                                .filter(t -> predicate.test(new RepositoryItemWithMethodArgs<>(t, args)))
                ).orElse(mainStream);

        // compute result (at the and data would be already altered)
        final Object result = resultTransformer.apply(filteredStream, args);

        final Stream<T> streamForRemoval = proxyState
                .getData()
                .stream();

        // filter data that are NOT marked for removal and collect new list
        final List<T> filteredData = filterPredicate
                .map(predicate ->
                        streamForRemoval
                                .filter(t -> !predicate.test(new RepositoryItemWithMethodArgs<>(t, args)))
                                .collect(Collectors.toCollection(() -> (List<T>)new LinkedList<T>()))
                ).orElse(proxyState.getData());

        proxyState.resetDataTo(filteredData);

        return result;
    }

}
