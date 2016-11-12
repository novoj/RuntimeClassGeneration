package cz.novoj.generation.contract;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.*;
import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;

public interface ProxyStateAccessor {

	/**
	 * Returns internal state of the proxy that is unique to each instance.
	 * @return
	 */
	Object getProxyState();

	/** METHOD CONTRACT: Object getProxyState() **/
	static MethodClassification<ProxyStateAccessor, Void, Object> getProxyStateMethodInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, ProxyStateAccessor.class, "getProxyState"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
		);
	}

}
