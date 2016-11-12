package cz.novoj.generation.proxyGenerator.infrastructure;

import java.lang.reflect.Method;

@FunctionalInterface
public interface CurriedMethodContextInvocationHandler<S, U> {

	/**
	 * This method is variant of {@link MethodInvocationHandler#invoke(Object, Method, Object[], Object, Object)} method,
	 * but except methodContext parameter which will be curried and remembered in the internal state of this lambda.
	 *
	 * @param proxy reference to the proxy instance
	 * @param method reference to the proxy method (or super method if supported)
	 * @param args arguments of method invocations
	 * @param proxyState references to the state object unique for each proxy instance
	 * @return
	 * @throws Throwable
	 */
	Object invoke(U proxy, Method method, Object[] args, S proxyState) throws Throwable;

}
