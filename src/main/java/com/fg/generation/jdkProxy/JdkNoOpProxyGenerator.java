package com.fg.generation.jdkProxy;

import lombok.Getter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface JdkNoOpProxyGenerator {

	static <T> T instantiate(Class<T> contract) {
		return (T)Proxy.newProxyInstance(
				JdkNoOpProxyGenerator.class.getClassLoader(),
				new Class[]{contract},
				new InvocationHandler() {
					@Getter private int called;

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						called++;
						return null;
					}
				}
		);
	}

}
