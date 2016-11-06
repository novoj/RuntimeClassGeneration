package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.GenericBucketRepository;
import cz.novoj.generation.contract.dao.executor.dto.DaoMethodQuery;
import cz.novoj.generation.contract.dao.executor.dto.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.executor.visitor.QueryNodeToComparatorVisitor;
import cz.novoj.generation.contract.dao.executor.visitor.QueryNodeToPredicateVisitor;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.model.PropertyAccessor;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public class GetDaoMethodExecutor<T extends PropertyAccessor> extends AbstractDaoMethodExecutor<T> {
    private final Optional<Predicate<RepositoryItemWithMethodArgs<T>>> filterPredicate;
    private final Optional<Comparator<T>> orderComparator;
    private final BiFunction<Stream<T>, Object[], ?> resultTransformer;

    public GetDaoMethodExecutor(Method method) {
        final DaoMethodQuery daoMethodQuery = getKeywordInstances(method.getName());

        this.filterPredicate = ofNullable(daoMethodQuery.getFilter()).map(keywordInstance -> getFilterPredicate(method, keywordInstance));
        this.orderComparator = ofNullable(daoMethodQuery.getSort()).map(this::getSortComparator);
        this.resultTransformer = getResultTransformer(method);
    }

    private Comparator<T> getSortComparator(QueryNode queryNode) {
        QueryNodeToComparatorVisitor<T> visitor = new QueryNodeToComparatorVisitor<>();
        queryNode.visit(visitor);
        return visitor.getComparator();
    }

    private Predicate<RepositoryItemWithMethodArgs<T>> getFilterPredicate(Method method, QueryNode queryNode) {
        QueryNodeToPredicateVisitor<T> visitor = new QueryNodeToPredicateVisitor<>(method);
        queryNode.visit(visitor);
        return visitor.getPredicate();
    }

    @Override
    public Object apply(GenericBucketRepository<T> proxyState, Object[] args) {
        final Stream<T> mainStream = proxyState
                .getData()
                .stream();

        // filter data if required
        final Stream<T> filteredStream = filterPredicate
                .map(predicate -> mainStream.filter(t -> predicate.test(new RepositoryItemWithMethodArgs<>(t, args))))
                .orElse(mainStream);

        // sort output if required
        final Stream<T> sortedStream = orderComparator
                .map(filteredStream::sorted)
                .orElse(filteredStream);

        // return required result type
        return resultTransformer
                .apply(sortedStream, args);
    }

}
