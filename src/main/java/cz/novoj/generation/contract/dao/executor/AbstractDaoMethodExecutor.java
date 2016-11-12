package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.GenericBucketRepository;
import cz.novoj.generation.contract.dao.executor.collector.QueryCollector;
import cz.novoj.generation.contract.dao.executor.dto.DaoMethodQuery;
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

import static cz.novoj.generation.contract.dao.GenericBucketDaoProxyGenerator.GET;
import static java.util.Optional.ofNullable;

abstract class AbstractDaoMethodExecutor<T extends PropertyAccessor> implements DaoMethodExecutor<GenericBucketRepository<T>> {

	/**
	 * Translates method name into query abstract syntax tree.
	 * It does that by splitting method name by character camel case and applying collector implementation.
	 * Using ANTLR would be more appropriate here. Used implementation is limited state machine in the form in collector.
	 *
	 * @param methodName
	 * @return
	 */
    static DaoMethodQuery getQueryAST(String methodName) {
        return Arrays
                .stream(StringUtils.splitByCharacterTypeCamelCase(methodName.substring(GET.length())))
                .collect(
					new QueryCollector(
						// default container and keyword for filtering
						FilterKeywordContainer.And, FilterKeyword.Eq,
						// default container and keyword for sorting
						SortKeywordContainer.And, SortKeyword.Asc
					)
                );
    }

	/**
	 * Creates new function for transforming stream of repository items into the requied return type.
	 * @param method
	 * @return
	 */
	BiFunction<Stream<T>, Object[], ?> getResultTransformer(Method method) {
        final Class<?> returnType = method.getReturnType();
        if (Void.class.equals(returnType)) {
        	// void is required - just return null
            return (tStream, args) -> null;
        } else if (List.class.equals(returnType) || Collection.class.equals(returnType)) {
        	// collection or list is required in return
            return (tStream, args) -> tStream.collect(Collectors.toList());
        } else if (Stream.class.equals(returnType)) {
        	// stream is required - we don't need to do much more
            return (tStream, args) -> tStream;
        } else if (returnType.isArray()) {
        	// array is required - create array of proper type
            return (tStream, objects) -> tStream.toArray(value -> (Object[]) Array.newInstance(returnType.getComponentType(), value));
        } else if (long.class.isAssignableFrom(returnType)) {
        	// item count is required - count them
            return (tStream, objects) -> tStream.count();
        } else if (int.class.isAssignableFrom(returnType)) {
			// item count is required - count them
            return (tStream, objects) -> Long.valueOf(tStream.count()).intValue();
        } else if (Boolean.class.equals(returnType)) {
			// boolean is required - let's return true if there is any item in the stream
            return (tStream, objects) -> tStream.count() > 0;
        } else {
        	// something else is required - we'll assume single item is required in some form
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
                            // ups - we may try to convert types here, if we wanted example to be more complex
                            throw new UnsupportedOperationException("Unsupported return type in method " + method.toGenericString());
                        }
                    }) /* null is always ok :) */.orElse(null);
                }
            };
        }
    }
}
