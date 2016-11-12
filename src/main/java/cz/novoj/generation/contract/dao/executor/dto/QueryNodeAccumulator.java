package cz.novoj.generation.contract.dao.executor.dto;

import cz.novoj.generation.contract.dao.query.instance.ContainerQueryNode;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import cz.novoj.generation.contract.dao.query.keyword.KeywordContainer;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * State object for the query parsing state maching.
 */
@Data
public class QueryNodeAccumulator {
    private final Keyword defaultKeyword;
    private final Keyword.Purpose purpose;
    private ContainerQueryNode rootKeyword;
    private LinkedList<String> words = new LinkedList<>();
    private LinkedList<Keyword> keywordAdepts = new LinkedList<>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int constantIndex;

    public QueryNodeAccumulator(KeywordContainer defaultContainer, Keyword defaultKeyword) {
        this.rootKeyword = new ContainerQueryNode(defaultContainer);
        this.defaultKeyword = defaultKeyword;
        this.purpose = defaultContainer.getPurpose();
    }

    public List<String> getWords() {
        return words;
    }

    public List<Keyword> getKeywordAdepts() {
        return keywordAdepts;
    }

    public void addKeywordAdept(Keyword keyword) {
        if (keywordAdepts.isEmpty() || !keywordAdepts.getLast().getType().equals(keyword.getType())) {
            keywordAdepts.add(keyword);
        } else {
            keywordAdepts.set(keywordAdepts.size() - 1, keyword);
        }
    }

    public void clear(String constantPart) {
        final Iterator<String> it = words.descendingIterator();
        int counter = constantPart.length();
        while (it.hasNext()) {
            final String constant = it.next();
            if (counter > 0) {
				counter -= constant.length();
                it.remove();
            }
            if (counter <= 0) {
                break;
            }
        }
    }

    public void clearKeywordAdepts() {
        this.keywordAdepts.clear();
    }

    public void addWord(String constant) {
        this.words.add(constant);
    }

    public String popConstant(String keywordName) {
        clear(keywordName);
        return popConstant();
    }

    public QueryNode getFinalKeyword() {
        if (this.rootKeyword.getSubKeywords().isEmpty()) {
            return null;
        } else if (this.rootKeyword.getSubKeywords().size() == 1) {
            return this.rootKeyword.getSubKeywords().get(0);
        } else {
            return this.rootKeyword;
        }
    }

    public void addKeywordContainerInstanceWithChild(KeywordContainer keywordContainer, Keyword keyword, String constant) {
        ContainerQueryNode containerInstance = new ContainerQueryNode(keywordContainer);
        containerInstance.addSubKeyword(new LeafQueryNode(keyword, constant, getConstantIndexAndIncrement(keyword)));
        addKeywordInstance(containerInstance);
    }

    public void addKeywordContainerInstance(KeywordContainer keywordContainer) {
        addKeywordInstance(
                new ContainerQueryNode(keywordContainer)
        );
    }

    public void addKeywordInstance(Keyword keyword, String constant) {
        addKeywordInstance(
                new LeafQueryNode(keyword, constant, getConstantIndexAndIncrement(keyword))
        );
    }

    public void addKeywordInstance(String constant) {
        addKeywordInstance(
                new LeafQueryNode(defaultKeyword, constant, getConstantIndexAndIncrement(defaultKeyword))
        );
    }

    private void addKeywordInstance(LeafQueryNode keywordInstance) {
        clear(keywordInstance.getConstant());
        this.rootKeyword.addSubKeyword(keywordInstance);
    }

    private void addKeywordInstance(ContainerQueryNode keywordInstance) {

        if (keywordInstance.getKeyword() instanceof FilterKeywordContainer &&
                ((FilterKeywordContainer) keywordInstance.getKeyword()).affectsNextKeyword()) {
            this.rootKeyword.addSubKeyword(keywordInstance);
        } else {
            if (this.rootKeyword.getKeyword() == keywordInstance.getKeyword()) {
                // do nothing - we have proper container at hand already
            } else if (this.rootKeyword.getSubKeywords().size() == 1) {
                keywordInstance.addSubKeyword(this.rootKeyword.getSubKeywords().get(0));
                this.rootKeyword = keywordInstance;
            } else {
                keywordInstance.addSubKeyword(this.rootKeyword);
                this.rootKeyword = keywordInstance;
            }
        }
    }

    private String popConstant() {
        final String constant = StringUtils.join(this.words, null);
        return StringUtils.uncapitalize(constant);
    }

    private Integer getConstantIndexAndIncrement(Keyword keyword) {
        if (keyword instanceof FilterKeyword && ((FilterKeyword) keyword).isRequiresArgument()) {
            return constantIndex++;
        } else {
            return null;
        }
    }

}
