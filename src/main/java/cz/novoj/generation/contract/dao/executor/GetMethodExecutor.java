package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.executor.helper.KeywordInstanceToComparatorVisitor;
import cz.novoj.generation.contract.dao.executor.helper.KeywordInstanceToPredicateVisitor;
import cz.novoj.generation.contract.dao.executor.helper.MethodQuery;
import cz.novoj.generation.contract.dao.executor.helper.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstance;
import cz.novoj.generation.contract.dao.repository.GenericBucketRepository;
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
public class GetMethodExecutor<T extends PropertyAccessor> extends AbstractMethodExecutor<T> {
    private final Optional<Predicate<RepositoryItemWithMethodArgs<T>>> filterPredicate;
    private final Optional<Comparator<T>> orderComparator;
    private final BiFunction<Stream<T>, Object[], ?> resultTransformer;

    public GetMethodExecutor(Method method) {
        final MethodQuery methodQuery = getKeywordInstances(method.getName());

        this.filterPredicate = ofNullable(methodQuery.getFilter()).map(keywordInstance -> getFilterPredicate(method, keywordInstance));
        this.orderComparator = ofNullable(methodQuery.getSort()).map(this::getSortComparator);
        this.resultTransformer = getResultTransformer(method);
    }

    private Comparator<T> getSortComparator(KeywordInstance keywordInstance) {
        KeywordInstanceToComparatorVisitor<T> visitor = new KeywordInstanceToComparatorVisitor<>();
        keywordInstance.visit(visitor);
        return visitor.getComparator();
    }

    private Predicate<RepositoryItemWithMethodArgs<T>> getFilterPredicate(Method method, KeywordInstance keywordInstance) {
        KeywordInstanceToPredicateVisitor<T> visitor = new KeywordInstanceToPredicateVisitor<>(method);
        keywordInstance.visit(visitor);
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
