package cz.novoj.generation.contract.dao.executor;

/**
 * Created by Rodina Novotnych on 06.11.2016.
 */
interface DaoMethodExecutor<T> {

    Object apply(T proxyState, Object[] args);

}
