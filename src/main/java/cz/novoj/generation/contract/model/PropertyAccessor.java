package cz.novoj.generation.contract.model;

import cz.novoj.generation.contract.dao.dto.FilterKeyword;
import cz.novoj.generation.contract.dao.dto.FilterKeywordContainer;
import cz.novoj.generation.contract.dao.helper.ReflectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface PropertyAccessor {
	Log log = LogFactory.getLog(PropertyAccessor.class);

	Object getProperty(String name);

	void setProperty(String name, Object value);

	Map<String, Object> getProperties();

	static <U extends PropertyAccessor> boolean matches(FilterKeyword keyword, U item, String propertyName, Object propertyValue) {
		final Optional<Object> existingPropertyValue = ofNullable(
			ofNullable(item.getProperty(propertyName)).orElseGet(() -> ReflectionUtils.getProperty(item, propertyName))
		);
		final boolean result;

		switch (keyword) {
			case Eq: result = existingPropertyValue.map(o -> o.equals(propertyValue)).orElse(false); break;
			case Contains: result = existingPropertyValue.map(o -> String.valueOf(o).contains(String.valueOf(propertyValue))).orElse(false); break;
			case LessThan: result = existingPropertyValue.map(o -> ((Comparable)o).compareTo(propertyValue) < 0).orElse(false); break;
			case LessThanEq: result = existingPropertyValue.map(o -> ((Comparable)o).compareTo(propertyValue) <= 0).orElse(false); break;
			case MoreThan: result = existingPropertyValue.map(o -> ((Comparable)o).compareTo(propertyValue) > 0).orElse(false); break;
			case MoreThanEq: result = existingPropertyValue.map(o -> ((Comparable)o).compareTo(propertyValue) >= 0).orElse(false); break;
			case IsNull: result = existingPropertyValue.map(o -> false).orElse(true); break;
			case In: {
				if (propertyValue == null || !existingPropertyValue.isPresent()) {
					result = false;
				} else {
					Object itemValue = existingPropertyValue.get();
					if (propertyValue.getClass().isArray()) {
						result = isPresentInStream(itemValue, Stream.of((Object[])propertyValue));
					} else if (propertyValue instanceof Collection) {
						result = isPresentInStream(itemValue, ((Collection)propertyValue).stream());
					} else if (propertyValue instanceof Iterable) {
						result = isPresentInStream(itemValue, StreamSupport.stream(((Iterable<U>)propertyValue).spliterator(), false));
					} else {
						result = itemValue.equals(propertyValue);
					}
				}
				break;
			}
			default: result = false;
		}

		log.debug(existingPropertyValue.orElse(null) + " " + keyword.name() + " " + propertyValue + " = " + result);

		return result;
	}

	static <T> Predicate<T> matches(FilterKeywordContainer keyword, Predicate<T> previousPredicate, Predicate<T> predicate) {
		switch (keyword) {
			case And: return ofNullable(previousPredicate).map(p -> p.and(predicate)).orElse(predicate);
			case Or: return ofNullable(previousPredicate).map(p -> p.or(predicate)).orElse(predicate);
			case Not: return predicate.negate();
		}
		return u -> false;
	}

	static <U> boolean isPresentInStream(Object itemValue, Stream<U> stream) {
		return stream.filter(o -> o.equals(itemValue)).findAny().isPresent();
	}
}
