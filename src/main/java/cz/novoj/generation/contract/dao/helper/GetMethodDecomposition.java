package cz.novoj.generation.contract.dao.helper;

import cz.novoj.generation.contract.dao.dto.*;
import cz.novoj.generation.contract.model.PropertyAccessor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public class GetMethodDecomposition<T extends PropertyAccessor> {

    private final Optional<Predicate<RepositoryItemWithMethodArgs<T>>> filterPredicate;
    private final Optional<Comparator<T>> orderComparator;
    private final BiFunction<Stream<T>, Object[], ?> resultTransformer;

    public GetMethodDecomposition(Method method) {
        final KeywordInstance keywords = getKeywordInstances(method.getName());

        this.filterPredicate = ofNullable(keywords).map(keywordInstance -> getFilterPredicate(method, keywordInstance));
        this.orderComparator = empty();
        this.resultTransformer = getResultTransformer(method);
    }

    private Predicate<RepositoryItemWithMethodArgs<T>> getFilterPredicate(Method method, KeywordInstance keywordInstance) {
        KeywordInstanceToPredicateVisitor visitor = new KeywordInstanceToPredicateVisitor(method);
        keywordInstance.visit(visitor);
        return visitor.getPredicate();
    }

    public static KeywordInstance getKeywordInstances(String methodName) {
        return Arrays
                .stream(StringUtils.splitByCharacterTypeCamelCase(methodName.substring("getBy".length())))
                .collect(new KeywordsInstanceCollector(new KeywordWithSubKeywords(FilterKeywordContainer.And)));
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

    @Data
    private static class RepositoryItemWithMethodArgs<S extends PropertyAccessor> {
        private final S repositoryItem;
        private final Object[] args;

    }

    private static class KeywordInstanceToPredicateVisitor<U extends PropertyAccessor> implements KeywordInstanceVisitor {
        private final Method method;
        private Predicate<RepositoryItemWithMethodArgs<U>> predicate;
        private Stack<Consumer<Predicate<RepositoryItemWithMethodArgs<U>>>> predicateConsumer = new Stack<>();

        public KeywordInstanceToPredicateVisitor(Method method) {
            this.method = method;
            predicateConsumer.push(p -> KeywordInstanceToPredicateVisitor.this.predicate = p);
        }

        @Override
        public void accept(KeywordWithConstant keywordInstance) {
            predicateConsumer.peek().accept(riwma -> {
                final U item = riwma.getRepositoryItem();
                final int argIndex = keywordInstance.getIndex();
                final Object[] args = riwma.getArgs();

                if (argIndex >= args.length) {
                    throw new IllegalArgumentException(
                            "No argument in method " + method.toGenericString() +
                                    " for " + keywordInstance.getConstant() + " filtering constraint!"
                    );
                }

                return PropertyAccessor.matches(
                        (FilterKeyword) keywordInstance.getKeyword(), item,
                        keywordInstance.getConstant(), args[argIndex]
                );
            });
        }

        @Override
        public void accept(KeywordWithSubKeywords keywordInstance) {

            ContainerPredicateConsumer<U> subKeywordPredicateConsumer =
                    new ContainerPredicateConsumer<>((FilterKeywordContainer) keywordInstance.getKeyword());


            predicateConsumer.push(subKeywordPredicateConsumer);
            for (KeywordInstance ki : keywordInstance.getSubKeywords()) {
                ki.visit(this);
            }
            predicateConsumer.pop();

            predicateConsumer.peek().accept(subKeywordPredicateConsumer.getFinalPredicate());

        }

        Predicate<RepositoryItemWithMethodArgs<U>> getPredicate() {
            return predicate;
        }

        @Data
        @RequiredArgsConstructor
        private static class ContainerPredicateConsumer<U extends PropertyAccessor> implements Consumer<Predicate<RepositoryItemWithMethodArgs<U>>> {
            private final FilterKeywordContainer keyword;
            private Predicate<RepositoryItemWithMethodArgs<U>> finalPredicate;

            @Override
            public void accept(Predicate<RepositoryItemWithMethodArgs<U>> predicate) {
                finalPredicate = PropertyAccessor.matches(keyword, finalPredicate, predicate);
            }
        }
    }
}
