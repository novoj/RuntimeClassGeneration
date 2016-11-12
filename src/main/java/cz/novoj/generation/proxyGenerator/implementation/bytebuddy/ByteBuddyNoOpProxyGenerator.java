package cz.novoj.generation.proxyGenerator.implementation.bytebuddy;

import java.lang.reflect.InvocationHandler;


public interface ByteBuddyNoOpProxyGenerator {
    InvocationHandler NULL_INVOCATION_HANDLER = (proxy, method, args) -> null;

    static <T> T instantiate(Class<T> contract) {
        return ByteBuddyProxyGenerator.instantiate(
                NULL_INVOCATION_HANDLER,
                contract
        );
    }

}
