package cz.novoj.generation.proxyGenerator.infrastructure;

import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
public interface ProxyStateAccessor {

    Object getProxyState();

    static MethodClassification<Void, Object, ProxyStateAccessor> getProxyStateMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, ProxyStateAccessor.class, "getProxyState"),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
        );
    }

}
