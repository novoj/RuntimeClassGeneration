package cz.novoj.generation.proxyGenerator.infrastructure;

@FunctionalInterface
public interface MethodInvocationHandler<U, T, S> {

	/**
	 * This method mimics {@link java.lang.reflect.InvocationHandler} interface but accepts memoized information
	 * of proxyState and methodContext.
	 *
	 * @param method reference to the proxy method (or super method if supported)
	 * @param proxy reference to the proxy instance
	 * @param args arguments of method invocations
	 * @param methodContext memoized method context that contains parsed information from method name, args, params etc.
	 * @param proxyState references to the state object unique for each proxy instance
	 * @return
	 * @throws Throwable
	 */
    Object invoke(MethodCall method, U proxy, Object[] args, T methodContext, S proxyState) throws Throwable;

}
