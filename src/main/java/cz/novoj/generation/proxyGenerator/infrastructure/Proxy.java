package cz.novoj.generation.proxyGenerator.infrastructure;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
public interface Proxy {

    Object getProxyState();

    static MethodClassification<Void, Object> getProxyStateMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Proxy.class.getDeclaredMethod("getProxyState")),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
        );
    }

}
