package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import java.lang.reflect.InvocationHandler;


public interface JdkNoOpProxyGenerator {
    InvocationHandler NULL_INVOCATION_HANDLER = (proxy, method, args) -> null;

    static <T> T instantiate(Class<T> contract) {
        return JdkProxyGenerator.instantiate(
                NULL_INVOCATION_HANDLER,
                contract
        );
    }

}
