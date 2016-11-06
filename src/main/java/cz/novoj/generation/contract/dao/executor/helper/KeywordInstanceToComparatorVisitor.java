package cz.novoj.generation.contract.dao.executor.helper;

import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstance;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstanceVisitor;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithConstant;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithSubKeywords;
import cz.novoj.generation.contract.dao.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.keyword.sort.SortKeywordContainer;
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
public class KeywordInstanceToComparatorVisitor<U extends PropertyAccessor> implements KeywordInstanceVisitor {
    @Getter private Comparator<U> comparator;
    private Stack<Consumer<Comparator<U>>> comparatorConsumer = new Stack<>();

    public KeywordInstanceToComparatorVisitor() {
        comparatorConsumer.push(p -> KeywordInstanceToComparatorVisitor.this.comparator = p);
    }

    @Override
    public void accept(KeywordWithConstant keywordInstance) {
        comparatorConsumer.peek().accept((o1, o2) -> PropertyAccessor.compare(
            (SortKeyword) keywordInstance.getKeyword(), o1, o2, keywordInstance.getConstant()
        ));
    }

    @Override
    public void accept(KeywordWithSubKeywords keywordInstance) {

        ContainerComparatorConsumer<U> subKeywordPredicateConsumer =
                new ContainerComparatorConsumer<>((SortKeywordContainer) keywordInstance.getKeyword());

        comparatorConsumer.push(subKeywordPredicateConsumer);
        for (KeywordInstance ki : keywordInstance.getSubKeywords()) {
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
