package com.fg.generation.jdkProxy;

import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JdkNoOpProxyGenerator {

	public static <T> T instantiate(Class<T> contract) {
		return (T)Proxy.newProxyInstance(
				JdkNoOpProxyGenerator.class.getClassLoader(),
				new Class[]{contract},
				new GenericBucketMaintainer()
		);
	}

	private static class GenericBucketMaintainer implements InvocationHandler {
		@Getter private int setterCalled;
		@Getter private int getterCalled;

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			final String methodName = method.getName();

			if(methodName.startsWith("get")) {
				getterCalled++;
			} else if(methodName.startsWith("set") && args.length == 1) {
				setterCalled++;
			}
			return null;
		}

	}
}
