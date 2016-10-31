package com.fg.generation.specific.javassist;

import javassist.util.proxy.MethodHandler;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface JavassistNoOpProxyGenerator {
    MethodHandler NULL_METHOD_HANDLER = (self, thisMethod, proceed, args) -> null;

    static <T> T instantiate(Class<T> contract) {
        return JavassistProxyGenerator.instantiate(
                NULL_METHOD_HANDLER,
                contract
        );
    }

}
