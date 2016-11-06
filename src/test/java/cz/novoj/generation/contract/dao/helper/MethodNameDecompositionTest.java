package cz.novoj.generation.contract.dao.helper;

import cz.novoj.generation.contract.dao.dto.*;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static cz.novoj.generation.contract.dao.helper.MethodNameDecomposition.getKeywordInstances;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public class MethodNameDecompositionTest {

    @Test
    public void GetMethodDecomposition_GetByFirstNameAndLastName_returnsKeywordInstance() throws Exception {
        KeywordInstance instances = getKeywordInstances("getByFirstNameAndLastName");
        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(instances), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(subKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByAge_returnsKeywordInstance() throws Exception {
        KeywordInstance instances = getKeywordInstances("getByAge");
        assertSubKeywordIsConstant(Collections.singletonList(instances), 0, FilterKeyword.Eq, "age");
    }

    @Test
    public void GetMethodDecomposition_GetByAgeLessThanEqAndFirstNameContainsOrLastName_returnsKeywordInstance() throws Exception {
        KeywordInstance instances = getKeywordInstances("getByAgeLessThanEqAndFirstNameContainsOrLastName");
        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(instances), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");

        List<KeywordInstance> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.LessThanEq, "age");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Contains, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByAgeLessThanAndFirstNameContainsOrLastNameEquals_returnsKeywordInstance() throws Exception {
        KeywordInstance instances = getKeywordInstances("getByAgeLessThanAndFirstNameContainsOrLastNameEq");
        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(instances), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");

        List<KeywordInstance> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.LessThan, "age");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Contains, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameOrLastNameAndAgeMoreThan_returnsKeywordInstance() throws Exception {
        KeywordInstance instances = getKeywordInstances("getByFirstNameOrLastNameAndAgeMoreThan");
        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(instances), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.MoreThan, "age");

        List<KeywordInstance> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameOrLastNameNotEquals_returnsKeywordInstance() throws Exception {
        KeywordInstance instances = getKeywordInstances("getByFirstNameOrLastNameNotEq");
        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(instances), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 0, FilterKeyword.Eq, "firstName");
        List<KeywordInstance> notSubKeywords = assertSubKeywordIsContainer(subKeywords, 1, FilterKeywordContainer.Not);
        assertSubKeywordIsConstant(notSubKeywords, 0, FilterKeyword.Eq, "lastName");
    }

    private List<KeywordInstance> assertSubKeywordIsContainer(List<KeywordInstance> instances, int index, FilterKeywordContainer keyword) {
        KeywordInstance keywordInstance = instances.get(index);
        assertTrue(keywordInstance instanceof KeywordWithSubKeywords);
        assertEquals(keyword, keywordInstance.getKeyword());
        return ((KeywordWithSubKeywords) keywordInstance).getSubKeywords();
    }

    private void assertSubKeywordIsConstant(List<KeywordInstance> subKeywords, int index, FilterKeyword keyword, String constant) {
        KeywordInstance keywordInstance = subKeywords.get(index);
        assertTrue(keywordInstance instanceof KeywordWithConstant);
        assertEquals(keyword, keywordInstance.getKeyword());
        assertEquals(constant, ((KeywordWithConstant)keywordInstance).getConstant());
    }

}