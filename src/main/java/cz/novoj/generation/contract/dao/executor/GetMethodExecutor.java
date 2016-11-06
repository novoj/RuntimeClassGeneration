package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.executor.helper.*;
import cz.novoj.generation.contract.dao.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstance;
import cz.novoj.generation.contract.dao.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.keyword.sort.SortKeywordContainer;
import cz.novoj.generation.contract.dao.repository.GenericBucketRepository;
import cz.novoj.generation.contract.model.PropertyAccessor;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public class GetMethodExecutor<T extends PropertyAccessor> {

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

    static MethodQuery getKeywordInstances(String methodName) {
        return Arrays
                .stream(StringUtils.splitByCharacterTypeCamelCase(methodName.substring("get".length())))
                .collect(
                    new KeywordsInstanceCollector(
                            FilterKeywordContainer.And, FilterKeyword.Eq,
                            SortKeywordContainer.And, SortKeyword.Asc
                    )
                );
    }

    private BiFunction<Stream<T>, Object[], ?> getResultTransformer(Method method) {
        final Class<?> returnType = method.getReturnType();
        if (Void.class.equals(returnType)) {
            return (tStream, args) -> null;
        } else if (List.class.equals(returnType) || Collection.class.equals(returnType)) {
            return (tStream, args) -> tStream.collect(Collectors.toList());
        } else if (Stream.class.equals(returnType)) {
            return (tStream, args) -> tStream;
        } else {
            return (tStream, args) -> {
                // stream should contain single item, otherwise reduce will be called and we'd generate exception
                T result = tStream.reduce((t, t2) -> {
                    throw new IllegalArgumentException(
                        "Found more than one result for a call of " + method.toGenericString() +
                                " with arguments " + StringUtils.join(args, ", ")
                    );
                }).orElse(null);

                // return result, but check cast (maybe method returns something else than has been found?!)
                final Optional<T> optionalResult = ofNullable(result);
                if (Optional.class.equals(returnType)) {
                    // here we should check whether optional accepts our result type
                    // but this is way too complex for this example - so let's get satisfied with ClassCastException possibility
                    return optionalResult;
                } else {
                    return optionalResult.map(t -> {
                        if (returnType.isInstance(t)) {
                            //ok type matches
                            return t;
                        } else {
                            // ups - we may try to convert types here, if watned example to be more complex
                            throw new UnsupportedOperationException("Unsupported return type in method " + method.toGenericString());
                        }
                    }) /* null is always ok :) */.orElse(null);
                }
            };
        }
    }

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
