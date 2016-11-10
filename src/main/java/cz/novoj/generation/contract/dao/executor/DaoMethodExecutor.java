package cz.novoj.generation.contract.dao.executor;

@FunctionalInterface
interface DaoMethodExecutor<T> {

	/**
	 * Applies arguments on repository item (ie. proxy).
	 * @param repositoryItem
	 * @param args
	 * @return
	 */
    Object apply(T repositoryItem, Object[] args);

}
