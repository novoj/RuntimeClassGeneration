package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import cz.novoj.generation.contract.ProxyStateAccessor;
import cz.novoj.generation.contract.StandardJavaMethods;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CommonsLog
public class JdkProxyDispatcherInvocationHandler<T> implements InvocationHandler {
	/* this cache might be somewhere else, but for the sake of the example ... */
	private static final Map<Method, CurriedMethodContextInvocationHandler<?, ?>> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);
	/* proxyState object unique to each proxy instance */
	private final T proxyState;
	/* ordered list of method classifications - ie atomic features of the proxy */
	private final List<MethodClassification<?, ?, ?>> methodClassifications = new LinkedList<>();

	public JdkProxyDispatcherInvocationHandler(T proxyState, MethodClassification<?, ?, ?>... methodClassifications) {
		this.proxyState = proxyState;
		// firstly add all standard Java Object features
		this.methodClassifications.add(StandardJavaMethods.hashCodeMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.equalsMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.toStringMethodInvoker());
		// then add infrastructural ProxyStateAccessor handling
		this.methodClassifications.add(ProxyStateAccessor.getProxyStateMethodInvoker());
		// finally add all method classifications developer wants
		Collections.addAll(this.methodClassifications, methodClassifications);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
		final CurriedMethodContextInvocationHandler invocationHandler = CLASSIFICATION_CACHE.computeIfAbsent(
				// CACHE KEY
				method,
				// LAMBDA THAT CREATES CURRIED METHOD INVOCATION HANDLER
				this::getCurriedMethodContextInvocationHandler
		);
		// INVOKE CURRIED LAMBDA
		return invocationHandler.invoke(proxy, method, args, proxyState);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private CurriedMethodContextInvocationHandler getCurriedMethodContextInvocationHandler(Method method) {
		log.info("Creating proxy method handler for " + method.toGenericString());
		return methodClassifications
				.stream()
				//find proper method classification (invoker handler) for passed method
				.filter(methodClassification -> methodClassification.matches(method))
				//create invocation handler with method context (invocation handler curried with method state)
				.map(methodClassification -> methodClassification.createMethodContext(method))
				//return first matching curried method context
				.findFirst()
				//return missing invocation handler throwing exception
				.orElse(StandardJavaMethods.missingImplementationInvoker());
	}

}
