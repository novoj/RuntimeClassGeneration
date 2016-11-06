package cz.novoj.generation.contract.dao.executor.helper;

import cz.novoj.generation.contract.dao.keyword.Keyword;
import cz.novoj.generation.contract.dao.keyword.KeywordContainer;
import cz.novoj.generation.contract.dao.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstance;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithConstant;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithSubKeywords;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
@Data
class KeywordInstanceAccumulator {
    private final Keyword defaultKeyword;
    private final Keyword.Kind kind;
    private KeywordWithSubKeywords rootKeyword;
    private LinkedList<String> words = new LinkedList<>();
    private LinkedList<Keyword> keywordAdepts = new LinkedList<>();
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private int constantIndex;

    KeywordInstanceAccumulator(KeywordContainer defaultContainer, Keyword defaultKeyword) {
        this.rootKeyword = new KeywordWithSubKeywords(defaultContainer);
        this.defaultKeyword = defaultKeyword;
        this.kind = defaultContainer.getKind();
    }

    List<String> getWords() {
        return words;
    }

    List<Keyword> getKeywordAdepts() {
        return keywordAdepts;
    }

    Integer getConstantIndexAndIncrement(Keyword keyword) {
        if (keyword instanceof FilterKeyword && ((FilterKeyword)keyword).isRequiresArgument()) {
            return constantIndex++;
        } else {
            return null;
        }
    }

    void addKeywordAdept(Keyword keyword) {
        if (keywordAdepts.isEmpty() || !keywordAdepts.getLast().getType().equals(keyword.getType())) {
            keywordAdepts.add(keyword);
        } else {
            keywordAdepts.set(keywordAdepts.size() - 1, keyword);
        }
    }

    void clear(String constantPart) {
        final Iterator<String> it = words.descendingIterator();
        int counter = constantPart.length();
        while (it.hasNext()) {
            final String constant = it.next();
            if (counter > 0) {
                counter = counter - constant.length();
                it.remove();
            }
            if (counter <= 0) {
                break;
            }
        }
    }

    void clearKeywordAdepts() {
        this.keywordAdepts.clear();
    }

    void addWord(String constant) {
        this.words.add(constant);
    }

    String popConstant(String keywordName) {
        clear(keywordName);
        return popConstant();
    }

    KeywordInstance getFinalKeyword() {
        if (this.rootKeyword.getSubKeywords().isEmpty()) {
            return null;
        } else if (this.rootKeyword.getSubKeywords().size() == 1) {
            return this.rootKeyword.getSubKeywords().get(0);
        } else {
            return this.rootKeyword;
        }
    }

    void addKeywordContainerInstanceWithChild(KeywordContainer keywordContainer, Keyword keyword, String constant) {
        KeywordWithSubKeywords containerInstance = new KeywordWithSubKeywords(keywordContainer);
        containerInstance.addSubKeyword(new KeywordWithConstant(keyword, constant, getConstantIndexAndIncrement(keyword)));
        addKeywordInstance(containerInstance);
    }

    void addKeywordContainerInstance(KeywordContainer keywordContainer) {
        addKeywordInstance(
                new KeywordWithSubKeywords(keywordContainer)
        );
    }

    void addKeywordInstance(Keyword keyword, String constant) {
        addKeywordInstance(
                new KeywordWithConstant(keyword, constant, getConstantIndexAndIncrement(keyword))
        );
    }

    void addKeywordInstance(String constant) {
        addKeywordInstance(
                new KeywordWithConstant(defaultKeyword, constant, getConstantIndexAndIncrement(defaultKeyword))
        );
    }

    private void addKeywordInstance(KeywordWithConstant keywordInstance) {
        clear(keywordInstance.getConstant());
        this.rootKeyword.addSubKeyword(keywordInstance);
    }

    private void addKeywordInstance(KeywordWithSubKeywords keywordInstance) {

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

}
