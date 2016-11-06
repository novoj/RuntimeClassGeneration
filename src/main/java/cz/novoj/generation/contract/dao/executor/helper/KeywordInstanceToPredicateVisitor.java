package cz.novoj.generation.contract.dao.executor.helper;

import cz.novoj.generation.contract.dao.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstance;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstanceVisitor;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithConstant;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithSubKeywords;
import cz.novoj.generation.contract.model.PropertyAccessor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
public class KeywordInstanceToPredicateVisitor<U extends PropertyAccessor> implements KeywordInstanceVisitor {
    private final Method method;
    @Getter private Predicate<RepositoryItemWithMethodArgs<U>> predicate;
    private Stack<Consumer<Predicate<RepositoryItemWithMethodArgs<U>>>> predicateConsumer = new Stack<>();

    public KeywordInstanceToPredicateVisitor(Method method) {
        this.method = method;
        predicateConsumer.push(p -> KeywordInstanceToPredicateVisitor.this.predicate = p);
    }

    @Override
    public void accept(KeywordWithConstant keywordInstance) {
        predicateConsumer.peek().accept(riwma -> {
            final U item = riwma.getRepositoryItem();
            final Object argument = Optional.ofNullable(keywordInstance.getIndex())
                    .map(argIndex -> {
                        final Object[] args = riwma.getArgs();

                        if (argIndex >= args.length) {
                            throw new IllegalArgumentException(
                                    "No argument in method " + method.toGenericString() +
                                            " for " + keywordInstance.getConstant() + " filtering constraint!"
                            );
                        }

                        return args[argIndex];
                    })
                    .orElse(null);
            return PropertyAccessor.matches(
                    (FilterKeyword) keywordInstance.getKeyword(), item,
                    keywordInstance.getConstant(), argument
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
