package cz.novoj.generation.proxyGenerator.infrastructure;

@FunctionalInterface
public interface CurriedMethodContextInvocationHandler<S, U> {

	/**
	 * This method is variant of {@link MethodInvocationHandler#invoke(MethodCall, Object, Object[], Object, Object)} method,
	 * but except methodContext parameter which will be curried and remembered in the internal state of this lambda.
	 *
	 * @param method reference to the proxy method (or super method if supported)
	 * @param proxy reference to the proxy instance
	 * @param args arguments of method invocations
	 * @param proxyState references to the state object unique for each proxy instance
	 * @return
	 * @throws Throwable
	 */
    Object invoke(MethodCall method, U proxy, Object[] args, S proxyState) throws Throwable;

}
