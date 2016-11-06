package cz.novoj.generation.proxyGenerator.infrastructure;

import static cz.novoj.generation.contract.dao.executor.helper.ReflectionUtils.isMethodDeclaredOn;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
public interface Proxy {

    Object getProxyState();

    static MethodClassification<Void, Object, Proxy> getProxyStateMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Proxy.class, "getProxyState"),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
        );
    }

}
