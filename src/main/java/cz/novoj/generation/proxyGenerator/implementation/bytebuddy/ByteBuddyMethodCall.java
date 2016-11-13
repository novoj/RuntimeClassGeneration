package cz.novoj.generation.proxyGenerator.implementation.bytebuddy;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodCall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ByteBuddy can call the very same method when super method code needs to be invoked.
 */
public class ByteBuddyMethodCall extends MethodCall {

	public ByteBuddyMethodCall(Object proxy, Method method, Object[] args) {
		super(proxy, method, args);
	}

	@Override
	public Object invokeSuper() {
		try {
			return method.invoke(proxy, args);
		} catch(InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
