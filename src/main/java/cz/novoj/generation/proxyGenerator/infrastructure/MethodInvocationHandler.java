package cz.novoj.generation.proxyGenerator.infrastructure;

import java.lang.reflect.Method;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@FunctionalInterface
public interface MethodInvocationHandler<T, S, U> {

    Object invoke(U proxy, Method method, Object[] args, T methodContext, S proxyState) throws Throwable;

}
