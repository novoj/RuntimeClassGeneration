package cz.novoj.generation.proxyGenerator.infrastructure;

import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;


public interface ProxyStateAccessor {

    Object getProxyState();

    static MethodClassification<ProxyStateAccessor, Void, Object> getProxyStateMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, ProxyStateAccessor.class, "getProxyState"),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
        );
    }

}
