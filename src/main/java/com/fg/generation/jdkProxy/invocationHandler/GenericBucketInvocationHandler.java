package com.fg.generation.jdkProxy.invocationHandler;

import org.apache.commons.lang.StringUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class GenericBucketInvocationHandler implements InvocationHandler {
	private final Map<String, Object> genericBucket = new HashMap<>(32);

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final String methodName = method.getName();

		if(method.isDefault()) {
			return invokeDefaultMethod(proxy, method, args);
		} else if("getProperties".equals(methodName)) {
			return genericBucket;
		} else if(methodName.startsWith("get")) {
			return genericBucket.get(
					StringUtils.uncapitalize(methodName.substring(3))
			);
		} else if(methodName.startsWith("set") && args.length == 1) {
			return genericBucket.put(
					StringUtils.uncapitalize(methodName.substring(3)), args[0]
			);
		} else {
			//do nothing
			return null;
		}
	}

	private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
		Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
		constructor.setAccessible(true);

		Class<?> declaringClass = method.getDeclaringClass();
		return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
						  .unreflectSpecial(method, declaringClass)
						  .bindTo(proxy)
						  .invokeWithArguments(args);
	}

}
