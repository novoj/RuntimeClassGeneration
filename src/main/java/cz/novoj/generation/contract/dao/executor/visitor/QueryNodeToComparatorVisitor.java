package cz.novoj.generation.contract.dao.executor.visitor;

import cz.novoj.generation.contract.dao.query.instance.ContainerQueryNode;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNodeVisitor;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeywordContainer;
import cz.novoj.generation.contract.model.PropertyAccessor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
public class QueryNodeToComparatorVisitor<U extends PropertyAccessor> implements QueryNodeVisitor {
    @Getter private Comparator<U> comparator;
    private Stack<Consumer<Comparator<U>>> comparatorConsumer = new Stack<>();

    public QueryNodeToComparatorVisitor() {
        comparatorConsumer.push(p -> QueryNodeToComparatorVisitor.this.comparator = p);
    }

    @Override
    public void accept(LeafQueryNode keywordInstance) {
        comparatorConsumer.peek().accept((o1, o2) -> PropertyAccessor.compare(
            (SortKeyword) keywordInstance.getKeyword(), o1, o2, keywordInstance.getConstant()
        ));
    }

    @Override
    public void accept(ContainerQueryNode keywordInstance) {

        ContainerComparatorConsumer<U> subKeywordPredicateConsumer =
                new ContainerComparatorConsumer<>((SortKeywordContainer) keywordInstance.getKeyword());

        comparatorConsumer.push(subKeywordPredicateConsumer);
        for (QueryNode ki : keywordInstance.getSubKeywords()) {
            ki.visit(this);
        }
        comparatorConsumer.pop();

        comparatorConsumer.peek().accept(subKeywordPredicateConsumer.getFinalComparator());

    }

    @Data
    @RequiredArgsConstructor
    private static class ContainerComparatorConsumer<U extends PropertyAccessor> implements Consumer<Comparator<U>> {
        private final SortKeywordContainer keyword;
        private Comparator<U> finalComparator;

        @Override
        public void accept(Comparator<U> comparator) {
            finalComparator = PropertyAccessor.compare(keyword, finalComparator, comparator);
        }
    }
}
