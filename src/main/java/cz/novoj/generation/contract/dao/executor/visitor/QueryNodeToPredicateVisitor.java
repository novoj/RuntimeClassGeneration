package cz.novoj.generation.contract.dao.executor.visitor;

import cz.novoj.generation.contract.dao.executor.dto.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.query.instance.ContainerQueryNode;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNodeVisitor;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class QueryNodeToPredicateVisitor<U extends PropertyAccessor> implements QueryNodeVisitor {
    private final Method method;
    @Getter private Predicate<RepositoryItemWithMethodArgs<U>> predicate;
    private Stack<Consumer<Predicate<RepositoryItemWithMethodArgs<U>>>> predicateConsumer = new Stack<>();

    public QueryNodeToPredicateVisitor(Method method) {
        this.method = method;
        predicateConsumer.push(p -> QueryNodeToPredicateVisitor.this.predicate = p);
    }

    @Override
    public void accept(LeafQueryNode keywordInstance) {
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
    public void accept(ContainerQueryNode keywordInstance) {

        ContainerPredicateConsumer<U> subKeywordPredicateConsumer =
                new ContainerPredicateConsumer<>((FilterKeywordContainer) keywordInstance.getKeyword());


        predicateConsumer.push(subKeywordPredicateConsumer);
        for (QueryNode ki : keywordInstance.getSubKeywords()) {
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
