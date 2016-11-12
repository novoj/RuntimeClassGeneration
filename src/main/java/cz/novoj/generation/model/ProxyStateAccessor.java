package cz.novoj.generation.model;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.*;
import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;

public interface ProxyStateAccessor {

    Object getProxyState();

    static MethodClassification<Void, Object, ProxyStateAccessor> getProxyStateMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, ProxyStateAccessor.class, "getProxyState"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxyStateAccessor, method, args, methodContext, proxyState) -> proxyState
        );
    }

}
