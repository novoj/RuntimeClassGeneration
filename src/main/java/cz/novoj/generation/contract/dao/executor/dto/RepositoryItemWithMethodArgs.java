package cz.novoj.generation.contract.dao.executor.dto;

import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Data;


@Data
public class RepositoryItemWithMethodArgs<S extends PropertyAccessor> {
    private final S repositoryItem;
    private final Object[] args;

}
