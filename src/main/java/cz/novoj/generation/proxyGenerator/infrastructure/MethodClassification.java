package cz.novoj.generation.proxyGenerator.infrastructure;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class MethodClassification<U, T, S> {
	/** no operation lambda creating void methodContext **/
	public static final Function<Method, Void> NO_CONTEXT = method -> null;

	/** this predicate checks method and returns true only if this classification should be applied on method **/
	private final Predicate<Method> methodMatcher;

	/** this factory function creates method context (ie. parsed data from method name, annotations and so on) **/
	private final Function<Method, T> methodContextFactory;

	/**
	 * this returns something like invocation handler but with extended method arguments:
	 *
	 * Object invoke(U proxy, Method method, Object[] args, T methodContext, S proxyState) throws Throwable;
	 *
	 * this method handler will be called by DispatcherInvocationHandler for each method execution on proxy
	 */
	private final MethodInvocationHandler<U, T, S> invocationHandler;

	/**
	 * Delegates matching logic to methodMatcher predicate.
	 * Law of Demeter in practise.
	 *
	 * @param method
	 * @return true if this classification should apply to passed method
	 */
	public boolean matches(Method method) {
		return methodMatcher.test(method);
	}

	/**
	 * Creates lambda function that wraps method context along with execution logic.
	 * It gets advantage of currying execution lambda with method context.
	 *
	 * @param classificationMethod
	 * @return lambda with method context baked in, so that only proxy, method and args are necessary to invoke logic
	 */
	public CurriedMethodContextInvocationHandler<S, U> createMethodContext(Method classificationMethod) {
		final T methodContext = methodContextFactory.apply(classificationMethod);
		return (proxy, executionMethod, args, proxyState) -> invocationHandler.invoke(
				proxy, executionMethod, args, methodContext, proxyState
		);
	}

}
