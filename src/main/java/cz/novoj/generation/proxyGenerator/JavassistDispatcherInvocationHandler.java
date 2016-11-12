package cz.novoj.generation.proxyGenerator;

import cz.novoj.generation.contract.ProxyStateAccessor;
import cz.novoj.generation.contract.StandardJavaMethods;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import javassist.util.proxy.MethodHandler;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CommonsLog
public class JavassistDispatcherInvocationHandler<T> implements MethodHandler {
	/* this cache might be somewhere else, but for the sake of the example ... */
	private static final Map<Method, CurriedMethodContextInvocationHandler<?, ?>> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);
	/* proxyState object unique to each proxy instance */
	private final T proxyState;
	/* ordered list of method classifications - ie atomic features of the proxy */
	private final List<MethodClassification<?, ?, ?>> methodClassifications = new LinkedList<>();

	public JavassistDispatcherInvocationHandler(T proxyState, MethodClassification<?, ?, ?>... methodClassifications) {
		this.proxyState = proxyState;
		// firstly add all standard Java Object features
		this.methodClassifications.add(StandardJavaMethods.hashCodeMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.equalsMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.toStringMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.defaultMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.realMethodInvoker());
		// then add infrastructural ProxyStateAccessor handling
		this.methodClassifications.add(ProxyStateAccessor.getProxyStateMethodInvoker());
		// finally add all method classifications developer wants
		Collections.addAll(this.methodClassifications, methodClassifications);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
		// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
		final CurriedMethodContextInvocationHandler invocationHandler =
				CLASSIFICATION_CACHE.computeIfAbsent(
						// CACHE KEY
						thisMethod,
						// LAMBDA THAT CREATES CURRIED METHOD INVOCATION HANDLER
						this::getCurriedMethodContextInvocationHandler
				);

		// INVOKE CURRIED LAMBDA, PASS REFERENCE TO REAL METHOD IF AVAILABLE
		return invocationHandler.invoke(self, proceed == null ? thisMethod : proceed, args, proxyState);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected CurriedMethodContextInvocationHandler getCurriedMethodContextInvocationHandler(Method method) {
		log.info("Creating proxy method handler for " + method.toGenericString());
		return methodClassifications.stream()
									//find proper method classification (invoker handler) for passed method
									.filter(methodClassification -> methodClassification.matches(method))
									//create curried invocation handler (invocation handler curried with method state)
									.map(methodClassification -> methodClassification.createMethodContext(method))
									//return first matching curried method context
									.findFirst()
									//return missing invocation handler throwing exception
									.orElse(StandardJavaMethods.missingImplementationInvoker());
	}

}
