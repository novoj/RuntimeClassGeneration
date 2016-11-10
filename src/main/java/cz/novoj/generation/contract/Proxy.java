package cz.novoj.generation.contract;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.*;
import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
public interface Proxy {

    Object getProxyState();

    static MethodClassification<Void, Object, Proxy> getProxyStateMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Proxy.class, "getProxyState"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
        );
    }

}
