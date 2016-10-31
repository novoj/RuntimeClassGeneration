package cz.novoj.generation.proxyGenerator.implementation.bytebuddy;

import java.lang.reflect.InvocationHandler;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface ByteBuddyNoOpProxyGenerator {
    InvocationHandler NULL_INVOCATION_HANDLER = (proxy, method, args) -> null;

    static <T> T instantiate(Class<T> contract) {
        return ByteBuddyProxyGenerator.instantiate(
                NULL_INVOCATION_HANDLER,
                contract
        );
    }

}
