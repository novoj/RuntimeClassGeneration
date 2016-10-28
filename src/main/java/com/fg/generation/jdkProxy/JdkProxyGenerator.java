package com.fg.generation.jdkProxy;

import com.fg.generation.jdkProxy.invocationHandler.infrastructure.DispatcherInvocationHandler;
import com.fg.generation.jdkProxy.invocationHandler.infrastructure.MethodClassification;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface JdkProxyGenerator {

	static <T, S> T instantiate(Class<T> contract, List<MethodClassification> methodClassifications, S proxyState) {
		return instantiate(contract, new DispatcherInvocationHandler<>(proxyState, methodClassifications));
	}

	@SuppressWarnings("unchecked")
	static <T> T instantiate(Class<T> contract, InvocationHandler invocationHandler) {
		return (T)Proxy.newProxyInstance(
				JdkProxyGenerator.class.getClassLoader(),
				new Class[]{contract},
				invocationHandler
		);
	}

}
