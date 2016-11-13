package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodCall;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Cglib library needs to use MethodProxy object when calling super method code.
 */
public class CglibMethodCall extends MethodCall {
	private final MethodProxy methodProxy;

	public CglibMethodCall(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
		super(proxy, method, args);
		this.methodProxy = methodProxy;
	}

	@Override
	public Object invokeSuper() {
		try {
			return methodProxy.invokeSuper(proxy, args);
		} catch(Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

}
