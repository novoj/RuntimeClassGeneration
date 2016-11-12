package cz.novoj.generation.contract.dao.executor.visitor;

import cz.novoj.generation.contract.dao.executor.dto.RepositoryItemWithMethodArgs;
import cz.novoj.generation.contract.dao.query.instance.LeafQueryNode;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeyword;
import cz.novoj.generation.contract.dao.query.keyword.filter.FilterKeywordContainer;
import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;

/**
 * Converts query nodes to predicates.
 */
public abstract class PredicateFactory {

	/**
	 * Creates simple {@link Predicate} for certain keyword, property name and value.
	 * @param queryNode
	 * @param <U>
	 * @return
	 */
	public static <U extends PropertyAccessor> Predicate<RepositoryItemWithMethodArgs<U>> createPredicate(LeafQueryNode queryNode) {
		final FilterKeyword keyword = (FilterKeyword)queryNode.getKeyword();
		final String propertyName = queryNode.getConstant();

		// translate keyword into predicate returning boolean
		switch (keyword) {
			case Eq: return u -> {
				final Object argument = getArgumentForPropertyName(queryNode, propertyName, u);
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> o.equals(argument)).orElse(false);
			};
			case Contains: return u -> {
				final Object argument = getArgumentForPropertyName(queryNode, propertyName, u);
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> String.valueOf(o).contains(String.valueOf(argument))).orElse(false);
			};
			case LessThan: return u -> {
				final Object argument = getArgumentForPropertyName(queryNode, propertyName, u);
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> ((Comparable)o).compareTo(argument) < 0).orElse(false);
			};
			case LessThanEq: return u -> {
				final Object argument = getArgumentForPropertyName(queryNode, propertyName, u);
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> ((Comparable)o).compareTo(argument) <= 0).orElse(false);
			};
			case MoreThan: return u -> {
				final Object argument = getArgumentForPropertyName(queryNode, propertyName, u);
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> ((Comparable)o).compareTo(argument) > 0).orElse(false);
			};
			case MoreThanEq: return u -> {
				final Object argument = getArgumentForPropertyName(queryNode, propertyName, u);
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> ((Comparable)o).compareTo(argument) >= 0).orElse(false);
			};
			case IsNull: return u -> {
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> false).orElse(true);
			};
			case IsNotNull: return u -> {
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				return existingValue.map(o -> true).orElse(false);
			};
			case In: return u -> {
				final Object argument = getArgumentForPropertyName(queryNode, propertyName, u);
				final Optional<Object> existingValue = getExistingPropertyValue(u.getRepositoryItem(), propertyName);
				if (argument == null || !existingValue.isPresent()) {
					return false;
				} else {
					Object itemValue = existingValue.get();
					if (argument.getClass().isArray()) {
						return isPresentInStream(itemValue, Stream.of((Object[])argument));
					} else if (argument instanceof Collection) {
						return isPresentInStream(itemValue, ((Collection)argument).stream());
					} else if (argument instanceof Iterable) {
						return isPresentInStream(itemValue, StreamSupport.stream(((Iterable<U>)argument).spliterator(), false));
					} else {
						return itemValue.equals(argument);
					}
				}
			};
			default: return u -> false;
		}
	}

	/**
	 * Combines two predicates together respecting keyword composition type.
	 * @param keyword
	 * @param previousPredicate
	 * @param predicate
	 * @param <U>
	 * @return
	 */
	public static <U extends PropertyAccessor> Predicate<RepositoryItemWithMethodArgs<U>> createPredicate(
			FilterKeywordContainer keyword, Predicate<RepositoryItemWithMethodArgs<U>> previousPredicate,
			Predicate<RepositoryItemWithMethodArgs<U>> predicate) {
		switch (keyword) {
			case And: return ofNullable(previousPredicate).map(p -> p.and(predicate)).orElse(predicate);
			case Or: return ofNullable(previousPredicate).map(p -> p.or(predicate)).orElse(predicate);
			case Not: return predicate.negate();
			default: return u -> false;
		}
	}

	/**
	 * Returns argument at certain index.
	 * @param queryNode
	 * @param propertyName
	 * @param u
	 * @param <U>
	 * @return
	 */
	private static <U extends PropertyAccessor> Object getArgumentForPropertyName(LeafQueryNode queryNode, String propertyName, RepositoryItemWithMethodArgs<U> u) {
		return ofNullable(queryNode.getIndex())
				.map(argIndex -> getArgumentFromIndex(propertyName, argIndex, u.getArgs()))
				.orElse(null);
	}

	/**
	 * Retrieves property value or required propertyName to compare from passed repository item.
	 * @param item
	 * @param propertyName
	 * @param <U>
	 * @return
	 */
	private static <U extends PropertyAccessor> Optional<Object> getExistingPropertyValue(U item, String propertyName) {
		return ofNullable(
				// lookup for property in our generic property accessor
				ofNullable(item.getProperty(propertyName))
						// if not found try to locate method on a proxy - property might be "computed"
						.orElseGet(() -> ReflectionUtils.getProperty(item, propertyName))
		);
	}

	/**
	 * Returns true if item value is present in the stream.
	 * @param itemValue
	 * @param stream
	 * @param <U>
	 * @return
	 */
	private static <U> boolean isPresentInStream(Object itemValue, Stream<U> stream) {
		return stream.anyMatch(o -> o.equals(itemValue));
	}

	/**
	 * Retrieves argument from method invocation arguments on specific position.
	 * @param propertyName
	 * @param argIndex
	 * @param args
	 * @return
	 */
	private static Object getArgumentFromIndex(String propertyName, Integer argIndex, Object[] args) {

		if (argIndex >= args.length) {
			throw new IllegalArgumentException(
					"No argument in method for " + propertyName + " filtering constraint!"
			);
		}

		return args[argIndex];
	}

}
