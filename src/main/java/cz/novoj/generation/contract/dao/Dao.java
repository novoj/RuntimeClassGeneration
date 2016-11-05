package cz.novoj.generation.contract.dao;

import java.util.List;

/**
 * Created by Rodina Novotnych on 31.10.2016.
 */
public interface Dao<T> {

    Class<T> getContractClass();

    T createNew();

    List<T> getAll();

}
