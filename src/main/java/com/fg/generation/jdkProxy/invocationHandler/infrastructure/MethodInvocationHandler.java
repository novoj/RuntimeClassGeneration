package com.fg.generation.jdkProxy.invocationHandler.infrastructure;

import java.lang.reflect.Method;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@FunctionalInterface
public interface MethodInvocationHandler<T, S> {

    Object invoke(Object proxy, Method method, Object[] args, T methodContext, S proxyState) throws Throwable;

}
