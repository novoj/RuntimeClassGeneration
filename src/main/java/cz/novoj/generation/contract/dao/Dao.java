package cz.novoj.generation.contract.dao;

import java.util.List;

/**
 * Generic DAO repository interface.
 * @param <T>
 */
public interface Dao<T> {

	/**
	 * Returns class of the items managed by this repository.
	 * @return
	 */
    Class<T> getContractClass();

	/**
	 * Creates new empty item of the type this repository works with.
	 * Item is only created, it is not added to the repository.
	 *
	 * @return
	 */
	T createNew();

	/**
	 * Adds new item of the target type to the repository.
	 * @param item
	 */
	void add(T item);

	/**
	 * Returns all items in the repoaitory.
	 * @return
	 */
    List<T> getAll();

}
