package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodCall;

import java.lang.reflect.Method;

/**
 * JdkProxy doesn't allow to call super method (there is none since JdkProxy only proxies interfaces).
 */
public class JdkProxyMethodCall extends MethodCall {

	public JdkProxyMethodCall(Object proxy, Method method, Object[] args) {
		super(proxy, method, args);
	}

	@Override
	public Object invokeSuper() {
		throw new UnsupportedOperationException("Calling super method is not allowed!");
	}

}
