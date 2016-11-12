package cz.novoj.generation.contract.dao.executor.visitor;

import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeyword;
import cz.novoj.generation.contract.dao.query.keyword.sort.SortKeywordContainer;
import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils;

import java.util.Comparator;

import static java.util.Optional.ofNullable;

/**
 * Converts query nodes to Comparators.
 */
public abstract class ComparatorFactory {

	/**
	 * Creates comparator for passed sort keyword and property name.
	 * @param keyword
	 * @param propertyName
	 * @param <U>
	 * @return
	 */
	public static <U extends PropertyAccessor> Comparator<U> compare(SortKeyword keyword, String propertyName) {
		switch (keyword) {
			case Asc: return (o1, o2) -> ((Comparable<Object>)getPropertyValue(o1, propertyName)).compareTo(getPropertyValue(o2, propertyName));
			case Desc: return (o1, o2) -> ((Comparable<Object>)getPropertyValue(o1, propertyName)).compareTo(getPropertyValue(o2, propertyName)) * -1;
		}
		return (o1, o2) -> 0;
	}

	/**
	 * Combines two comparators together respecting keyword composition type.
	 * @param keyword
	 * @param previousComparator
	 * @param comparator
	 * @param <U>
	 * @return
	 */
	public static <U extends PropertyAccessor> Comparator<U> compare(SortKeywordContainer keyword, Comparator<U> previousComparator, Comparator<U> comparator) {
		switch (keyword) {
			case And: return ofNullable(previousComparator).map(c -> c.thenComparing(comparator)).orElse(comparator);
		}
		return (o1, o2) -> 0;
	}

	/**
	 * Retrieves value for required property from the object.
	 * @param item
	 * @param propertyName
	 * @param <U>
	 * @return
	 */
	private static <U extends PropertyAccessor> Object getPropertyValue(U item, String propertyName) {
		return ofNullable(
				ofNullable(item.getProperty(propertyName)).orElseGet(() -> ReflectionUtils.getProperty(item, propertyName))
		).orElseThrow(() -> new NullPointerException("Cannot compare - property " + propertyName + " is null on object " + item + "!"));
	}


}
