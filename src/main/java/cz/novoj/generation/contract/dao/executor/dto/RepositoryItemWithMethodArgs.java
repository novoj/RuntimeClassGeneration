package cz.novoj.generation.contract.dao.executor.dto;

import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Data;

/**
 * Simple DTO wrapping repository item with method invocation arguments.
 * This class is required when we want to use {@link java.util.function.Predicate} abstraction that accepts only
 * single argument.
 *
 * This represents slight performance overhead, that we want to get rid of in production environment.
 *
 * @param <S>
 */
@Data
public class RepositoryItemWithMethodArgs<S extends PropertyAccessor> {
    private final S repositoryItem;
    private final Object[] args;

}
