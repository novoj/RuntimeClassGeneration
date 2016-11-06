package cz.novoj.generation.contract.dao.repository;

import cz.novoj.generation.contract.model.PropertyAccessor;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
public class GenericBucketRepository<T extends PropertyAccessor> {
    @Getter private List<T> data = new LinkedList<>();

    public void add(T item) {
        data.add(item);
    }

    public void resetDataTo(List<T> newData) {
        this.data = newData;
    }

}
