package cz.novoj.generation.contract.dao.executor;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
interface MethodExecutor<T> {

    Object apply(T proxyState, Object[] args);

}
