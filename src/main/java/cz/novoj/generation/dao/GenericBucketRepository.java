package cz.novoj.generation.dao;

import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
public class GenericBucketRepository<T extends PropertyAccessor> {
    @Getter @Setter private List<T> data = new LinkedList<T>();

    public void add(T item) {
        data.add(item);
    }

}
