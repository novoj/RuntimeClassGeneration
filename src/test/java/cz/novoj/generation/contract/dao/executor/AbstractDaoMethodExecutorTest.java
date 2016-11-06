package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.executor.dto.DaoMethodQuery;
import cz.novoj.generation.contract.dao.query.instance.ContainerQueryNode;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.instance.QueryNode;
import cz.novoj.generation.contract.dao.query.keyword.Keyword;
import cz.novoj.generation.contract.dao.query.keyword.KeywordContainer;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeywordContainer;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static cz.novoj.generation.contract.dao.executor.GetDaoMethodExecutor.getKeywordInstances;
import static org.junit.Assert.*;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public class AbstractDaoMethodExecutorTest {

    @Test
    public void GetMethodDecomposition_GetByFirstNameAndLastName_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByFirstNameAndLastName");
        QueryNode filter = daoMethodQuery.getFilter();
        assertNull(daoMethodQuery.getSort());

        List<QueryNode> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(subKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByAge_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByAge");
        QueryNode filter = daoMethodQuery.getFilter();
        assertNull(daoMethodQuery.getSort());
        assertSubKeywordIsConstant(Collections.singletonList(filter), 0, FilterKeyword.Eq, "age");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameIsNull_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByFirstNameIsNull");
        QueryNode filter = daoMethodQuery.getFilter();
        assertNull(daoMethodQuery.getSort());
        assertSubKeywordIsConstant(Collections.singletonList(filter), 0, FilterKeyword.IsNull, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByAgeLessThanEqAndFirstNameContainsOrLastName_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByAgeLessThanEqAndFirstNameContainsOrLastName");
        QueryNode filter = daoMethodQuery.getFilter();
        assertNull(daoMethodQuery.getSort());
        List<QueryNode> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");

        List<QueryNode> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.LessThanEq, "age");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Contains, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByAgeLessThanAndFirstNameContainsOrLastNameEquals_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByAgeLessThanAndFirstNameContainsOrLastNameEq");
        QueryNode filter = daoMethodQuery.getFilter();
        assertNull(daoMethodQuery.getSort());

        List<QueryNode> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.Eq, "lastName");

        List<QueryNode> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.LessThan, "age");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Contains, "firstName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameOrLastNameAndAgeMoreThan_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByFirstNameOrLastNameAndAgeMoreThan");
        QueryNode filter = daoMethodQuery.getFilter();
        assertNull(daoMethodQuery.getSort());

        List<QueryNode> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(subKeywords, 1, FilterKeyword.MoreThan, "age");

        List<QueryNode> andSubKeywords = assertSubKeywordIsContainer(subKeywords, 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(andSubKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(andSubKeywords, 1, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameOrLastNameNotEquals_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByFirstNameOrLastNameNotEq");
        QueryNode filter = daoMethodQuery.getFilter();
        assertNull(daoMethodQuery.getSort());

        List<QueryNode> subKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.Or);
        assertSubKeywordIsConstant(subKeywords, 0, FilterKeyword.Eq, "firstName");
        List<QueryNode> notSubKeywords = assertSubKeywordIsContainer(subKeywords, 1, FilterKeywordContainer.Not);
        assertSubKeywordIsConstant(notSubKeywords, 0, FilterKeyword.Eq, "lastName");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameSortedByAgeDesc_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByFirstNameSortedByAge");

        QueryNode filter = daoMethodQuery.getFilter();
        assertSubKeywordIsConstant(Collections.singletonList(filter), 0, FilterKeyword.Eq, "firstName");

        QueryNode sort = daoMethodQuery.getSort();
        assertSubKeywordIsConstant(Collections.singletonList(sort), 0, SortKeyword.Asc, "age");
    }

    @Test
    public void GetMethodDecomposition_GetByFirstNameAndAgeLessThanSortedByAgeDescAndLastNameAsc_returnsKeywordInstance() throws Exception {
        DaoMethodQuery daoMethodQuery = getKeywordInstances("getByFirstNameEqAndAgeLessThanSortedByAgeDescAndLastNameAsc");

        QueryNode filter = daoMethodQuery.getFilter();
        List<QueryNode> filterSubKeywords = assertSubKeywordIsContainer(Collections.singletonList(filter), 0, FilterKeywordContainer.And);
        assertSubKeywordIsConstant(filterSubKeywords, 0, FilterKeyword.Eq, "firstName");
        assertSubKeywordIsConstant(filterSubKeywords, 1, FilterKeyword.LessThan, "age");

        QueryNode sort = daoMethodQuery.getSort();
        List<QueryNode> sortSubKeywords = assertSubKeywordIsContainer(Collections.singletonList(sort), 0, SortKeywordContainer.And);
        assertSubKeywordIsConstant(sortSubKeywords, 0, SortKeyword.Desc, "age");
        assertSubKeywordIsConstant(sortSubKeywords, 1, SortKeyword.Asc, "lastName");
    }

    private List<QueryNode> assertSubKeywordIsContainer(List<QueryNode> instances, int index, KeywordContainer keyword) {
        QueryNode queryNode = instances.get(index);
        assertTrue(queryNode instanceof ContainerQueryNode);
        assertEquals(keyword, queryNode.getKeyword());
        return ((ContainerQueryNode) queryNode).getSubKeywords();
    }

    private void assertSubKeywordIsConstant(List<QueryNode> subKeywords, int index, Keyword keyword, String constant) {
        QueryNode queryNode = subKeywords.get(index);
        assertTrue(queryNode instanceof LeafQueryNode);
        assertEquals(keyword, queryNode.getKeyword());
        assertEquals(constant, ((LeafQueryNode) queryNode).getConstant());
    }

}