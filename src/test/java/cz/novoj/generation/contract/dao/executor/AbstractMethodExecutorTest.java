package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.executor.helper.MethodQuery;
import cz.novoj.generation.contract.dao.keyword.Keyword;
import cz.novoj.generation.contract.dao.keyword.KeywordContainer;
import cz.novoj.generation.contract.dao.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordInstance;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithConstant;
import cz.novoj.generation.contract.dao.keyword.instance.KeywordWithSubKeywords;
import cz.novoj.generation.contract.dao.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.keyword.sort.SortKeywordContainer;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static cz.novoj.generation.contract.dao.executor.GetMethodExecutor.getKeywordInstances;
import static org.junit.Assert.*;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public class AbstractMethodExecutorTest {

    @Test
    public void GetMethodDecomposition_GetByFirstNameAndLastName_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByFirstNameAndLastName");
        KeywordInstance filter = methodQuery.getFilter();
        assertNull(methodQuery.getSort());

        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(subKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByAge_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByAge");
        KeywordInstance filter = methodQuery.getFilter();
        assertNull(methodQuery.getSort());
        assertSubKeywordIsConstant(Collections.singletonList(filter), 0, FilterKeyword.Eq, "age");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameIsNull_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByFirstNameIsNull");
        KeywordInstance filter = methodQuery.getFilter();
        assertNull(methodQuery.getSort());
        assertSubKeywordIsConstant(Collections.singletonList(filter), 0, FilterKeyword.IsNull, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByAgeLessThanEqAndFirstNameContainsOrLastName_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByAgeLessThanEqAndFirstNameContainsOrLastName");
        KeywordInstance filter = methodQuery.getFilter();
        assertNull(methodQuery.getSort());
        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");

        List<KeywordInstance> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.LessThanEq, "age");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Contains, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByAgeLessThanAndFirstNameContainsOrLastNameEquals_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByAgeLessThanAndFirstNameContainsOrLastNameEq");
        KeywordInstance filter = methodQuery.getFilter();
        assertNull(methodQuery.getSort());

        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");

        List<KeywordInstance> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.LessThan, "age");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Contains, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameOrLastNameAndAgeMoreThan_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByFirstNameOrLastNameAndAgeMoreThan");
        KeywordInstance filter = methodQuery.getFilter();
        assertNull(methodQuery.getSort());

        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.MoreThan, "age");

        List<KeywordInstance> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameOrLastNameNotEquals_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByFirstNameOrLastNameNotEq");
        KeywordInstance filter = methodQuery.getFilter();
        assertNull(methodQuery.getSort());

        List<KeywordInstance> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 0, FilterKeyword.Eq, "firstName");
        List<KeywordInstance> notSubKeywords = assertSubKeywordIsContainer(subKeywords, 1, FilterKeywordContainer.Not);
        assertSubKeywordIsConstant(notSubKeywords, 0, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameSortedByAgeDesc_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByFirstNameSortedByAge");

        KeywordInstance filter = methodQuery.getFilter();
        assertSubKeywordIsConstant(Collections.singletonList(filter), 0, FilterKeyword.Eq, "firstName");

        KeywordInstance sort = methodQuery.getSort();
        assertSubKeywordIsConstant(Collections.singletonList(sort), 0, SortKeyword.Asc, "age");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameAndAgeLessThanSortedByAgeDescAndLastNameAsc_returnsKeywordInstance() throws Exception {
        MethodQuery methodQuery = getKeywordInstances("getByFirstNameEqAndAgeLessThanSortedByAgeDescAndLastNameAsc");

        KeywordInstance filter = methodQuery.getFilter();
        List<KeywordInstance> filterSubKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(filterSubKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(filterSubKeywords, 1, FilterKeyword.LessThan, "age");

        KeywordInstance sort = methodQuery.getSort();
        List<KeywordInstance> sortSubKeywords = assertSubKeywordIsContainer(Collections.singletonList(sort), 0, SortKeywordContainer.And);
        assertSubKeywordIsConstant(sortSubKeywords, 0, SortKeyword.Desc, "age");
        assertSubKeywordIsConstant(sortSubKeywords, 1, SortKeyword.Asc, "lastName");
    }

    private List<KeywordInstance> assertSubKeywordIsContainer(List<KeywordInstance> instances, int index, KeywordContainer keyword) {
        KeywordInstance keywordInstance = instances.get(index);
        assertTrue(keywordInstance instanceof KeywordWithSubKeywords);
        assertEquals(keyword, keywordInstance.getKeyword());
        return ((KeywordWithSubKeywords) keywordInstance).getSubKeywords();
    }

    private void assertSubKeywordIsConstant(List<KeywordInstance> subKeywords, int index, Keyword keyword, String constant) {
        KeywordInstance keywordInstance = subKeywords.get(index);
        assertTrue(keywordInstance instanceof KeywordWithConstant);
        assertEquals(keyword, keywordInstance.getKeyword());
        assertEquals(constant, ((KeywordWithConstant)keywordInstance).getConstant());
    }

}