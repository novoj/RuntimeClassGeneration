package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodCall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Javassist uses proceed reference to invoke super method logic.
 */
public class JavassistMethodCall extends MethodCall {
	private final Method proceed;

	public JavassistMethodCall(Object proxy, Method method, Object[] args, Method proceed) {
		super(proxy, method, args);
		this.proceed = proceed;
	}

	@Override
	public Object invokeSuper() {
		if (proceed == null) {
			throw new UnsupportedOperationException("Calling super method is not allowed!");
		} else {
			try {
				return proceed.invoke(proxy, args);
			} catch(InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
