package cz.novoj.generation.proxyGenerator.infrastructure;

import java.lang.reflect.Method;

@FunctionalInterface
public interface MethodInvocationHandler<U, T, S> {

	/**
	 * This method mimics {@link java.lang.reflect.InvocationHandler} interface but accepts memoized information
	 * of proxyState and methodContext.
	 *
	 * @param proxy reference to the proxy instance
	 * @param method reference to the proxy method (or super method if supported)
	 * @param args arguments of method invocations
	 * @param methodContext memoized method context that contains parsed information from method name, args, params etc.
	 * @param proxyState references to the state object unique for each proxy instance
	 * @return
	 * @throws Throwable
	 */
    Object invoke(U proxy, Method method, Object[] args, T methodContext, S proxyState) throws Throwable;

}
