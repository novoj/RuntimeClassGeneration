package cz.novoj.generation.proxyGenerator.infrastructure;

import java.lang.reflect.Method;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@FunctionalInterface
public interface ContextWiseMethodInvocationHandler<S, U> {

    Object invoke(U proxy, Method method, Object[] args, S proxyState) throws Throwable;

}
