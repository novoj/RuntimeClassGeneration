package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.GenericBucketDaoProxyGenerator;
import cz.novoj.generation.contract.dao.GenericBucketRepository;
import cz.novoj.generation.contract.dao.executor.dto.DaoMethodQuery;
import cz.novoj.generation.contract.dao.executor.collector.QueryCollector;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeywordContainer;
import cz.novoj.generation.model.traits.PropertyAccessor;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;


abstract class AbstractDaoMethodExecutor<T extends PropertyAccessor> implements DaoMethodExecutor<GenericBucketRepository<T>> {

    static DaoMethodQuery getKeywordInstances(String methodName) {
        return Arrays
                .stream(StringUtils.splitByCharacterTypeCamelCase(methodName.substring(GenericBucketDaoProxyGenerator.GET.length())))
                .collect(
                        new QueryCollector(
                                FilterKeywordContainer.And, FilterKeyword.Eq,
                                SortKeywordContainer.And, SortKeyword.Asc
                        )
                );
    }

    BiFunction<Stream<T>, Object[], ?> getResultTransformer(Method method) {
        final Class<?> returnType = method.getReturnType();
        if (Void.class.equals(returnType)) {
            return (tStream, args) -> null;
        } else if (List.class.equals(returnType) || Collection.class.equals(returnType)) {
            return (tStream, args) -> tStream.collect(Collectors.toList());
        } else if (Stream.class.equals(returnType)) {
            return (tStream, args) -> tStream;
        } else if (returnType.isArray()) {
            return (tStream, objects) -> tStream.toArray(value -> (Object[]) Array.newInstance(returnType.getComponentType(), value));
        } else if (long.class.isAssignableFrom(returnType)) {
            return (tStream, objects) -> tStream.count();
        } else if (int.class.isAssignableFrom(returnType)) {
            return (tStream, objects) -> Long.valueOf(tStream.count()).intValue();
        } else if (Boolean.class.equals(returnType)) {
            return (tStream, objects) -> tStream.count() > 0;
        } else {
            return (tStream, args) -> {
                // stream should contain single item, otherwise reduce will be called and we'd generate exception
                PropertyAccessor result = tStream.reduce((t, t2) -> {
                    throw new IllegalArgumentException(
                        "Found more than one result for a call of " + method.toGenericString() +
                                " with arguments " + StringUtils.join(args, ", ")
                    );
                }).orElse(null);

                // return result, but check cast (maybe method returns something else than has been found?!)
                final Optional optionalResult = ofNullable(result);
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
}
