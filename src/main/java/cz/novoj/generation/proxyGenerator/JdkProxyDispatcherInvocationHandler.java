package cz.novoj.generation.proxyGenerator;

import cz.novoj.generation.contract.Proxy;
import cz.novoj.generation.contract.StandardJavaMethods;
import cz.novoj.generation.proxyGenerator.infrastructure.ContextWiseMethodInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@CommonsLog
public class JdkProxyDispatcherInvocationHandler<T> implements InvocationHandler {
	protected final T proxyState;
	protected final LinkedList<MethodClassification> methodClassifications;

    /* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, ContextWiseMethodInvocationHandler> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);

	public JdkProxyDispatcherInvocationHandler(T proxyState, MethodClassification... methodClassifications) {
		this.proxyState = proxyState;
		this.methodClassifications = new LinkedList<>();
		this.methodClassifications.add(StandardJavaMethods.hashCodeMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.equalsMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.toStringMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.defaultMethodInvoker());
		this.methodClassifications.add(StandardJavaMethods.realMethodInvoker());
		this.methodClassifications.add(Proxy.getProxyStateMethodInvoker());
		Collections.addAll(this.methodClassifications, methodClassifications);
	}

	protected ContextWiseMethodInvocationHandler getContextWiseMethodInvocationHandler(Method method) {
		log.info("Creating proxy method handler for " + method.toGenericString());
		return methodClassifications.stream()
									//find proper method classification (invoker handler) for passed method
									.filter(methodClassification -> methodClassification.matches(method))
									//create contextwise invocation handler (invocation handler curried with method state)
									.map(methodClassification -> methodClassification.createContextInvocationHandler(method))
									.findFirst()
									//return missing invocation handler throwing exception
									.orElse(StandardJavaMethods.missingImplementationInvoker());
	}

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final ContextWiseMethodInvocationHandler invocationHandler =
                CLASSIFICATION_CACHE.computeIfAbsent(
                        method,
                        this::getContextWiseMethodInvocationHandler
                );

        return invocationHandler.invoke(proxy, method, args, proxyState);
    }

}
