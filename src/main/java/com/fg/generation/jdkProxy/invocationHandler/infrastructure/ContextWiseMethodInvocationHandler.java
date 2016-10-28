package com.fg.generation.jdkProxy.invocationHandler.infrastructure;

import java.lang.reflect.Method;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@FunctionalInterface
public interface ContextWiseMethodInvocationHandler<S> {

    Object invoke(Object proxy, Method method, Object[] args, S proxyState) throws Throwable;

}
