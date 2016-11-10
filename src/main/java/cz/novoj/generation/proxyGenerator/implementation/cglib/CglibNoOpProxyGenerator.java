package cz.novoj.generation.proxyGenerator.implementation.cglib;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface CglibNoOpProxyGenerator {
    MethodInterceptor NULL_METHOD_HANDLER = (o, method, objects, methodProxy) -> null;

    static <T> T instantiate(Class<T> contract) {
        return CglibProxyGenerator.instantiate(
                NULL_METHOD_HANDLER,
                contract
        );
    }

}
