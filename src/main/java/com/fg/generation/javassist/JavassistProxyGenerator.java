package com.fg.generation.javassist;

import com.fg.generation.infrastructure.DispatcherInvocationHandler;
import com.fg.generation.infrastructure.MethodClassification;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import java.util.List;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface JavassistProxyGenerator {

	static <T, S> T instantiate(Class<T> contract, List<MethodClassification> methodClassifications, S proxyState) {
		return instantiate(contract, new DispatcherInvocationHandler<>(proxyState, methodClassifications));
	}

	@SuppressWarnings("unchecked")
	static <T> T instantiate(Class<T> contract, MethodHandler methodHandler) {
        try {
            ProxyFactory f = new ProxyFactory();
            if (contract.isInterface()) {
                f.setInterfaces(new Class[]{contract, com.fg.generation.infrastructure.Proxy.class});
            } else {
                f.setSuperclass(contract);
                f.setInterfaces(new Class[]{com.fg.generation.infrastructure.Proxy.class});
            }

            f.setFilter(m -> !java.util.Objects.equals(m.getName(), "finalize"));

            Class c = f.createClass();

            T proxy = (T) c.newInstance();
            ((Proxy) proxy).setHandler(methodHandler);

            return proxy;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
    }

}
