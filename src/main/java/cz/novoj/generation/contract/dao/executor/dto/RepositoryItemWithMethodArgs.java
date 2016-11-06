package cz.novoj.generation.contract.dao.executor.dto;

import cz.novoj.generation.contract.model.PropertyAccessor;
import lombok.Data;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
@Data
public class RepositoryItemWithMethodArgs<S extends PropertyAccessor> {
    private final S repositoryItem;
    private final Object[] args;

}
