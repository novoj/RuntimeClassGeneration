package cz.novoj.generation.contract.dao.executor;

@FunctionalInterface
interface DaoMethodExecutor<T> {

	/**
	 * Applies method call arguments on proxy state (ie. repository or repository item).
	 * @param proxyState
	 * @param args
	 * @return
	 */
    Object apply(T proxyState, Object... args);

}
