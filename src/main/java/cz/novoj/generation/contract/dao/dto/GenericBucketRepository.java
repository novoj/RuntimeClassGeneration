package cz.novoj.generation.contract.dao.dto;

import cz.novoj.generation.contract.model.PropertyAccessor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
public class GenericBucketRepository<T extends PropertyAccessor> {
    @Getter private final List<T> data = new LinkedList<T>();

    public void add(T item) {
        data.add(item);
    }

}