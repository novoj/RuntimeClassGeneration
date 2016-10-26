package com.fg.generation.jdkProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JdkProxyGenerator {

	public static <T> T instantiate(Class<T> contract, InvocationHandler invocationHandler) {
		return (T)Proxy.newProxyInstance(
				JdkProxyGenerator.class.getClassLoader(),
				new Class[]{contract},
				invocationHandler
		);
	}

}
