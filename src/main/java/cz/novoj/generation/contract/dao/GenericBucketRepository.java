package cz.novoj.generation.contract.dao;

import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("InstanceMethodNamingConvention")
public class GenericBucketRepository<T extends PropertyAccessor> {
    @Getter private List<T> data = new LinkedList<>();

    public void add(T item) {
        data.add(item);
    }

}
