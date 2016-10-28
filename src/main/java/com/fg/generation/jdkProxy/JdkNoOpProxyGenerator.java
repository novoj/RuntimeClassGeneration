package com.fg.generation.jdkProxy;

import java.lang.reflect.InvocationHandler;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface JdkNoOpProxyGenerator {
    InvocationHandler NULL_INVOCATION_HANDLER = (proxy, method, args) -> null;

    static <T> T instantiate(Class<T> contract) {
        return JdkProxyGenerator.instantiate(
                NULL_INVOCATION_HANDLER,
                contract
        );
    }

}
