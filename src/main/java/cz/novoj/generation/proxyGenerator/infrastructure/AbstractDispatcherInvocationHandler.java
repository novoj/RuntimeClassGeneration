package cz.novoj.generation.proxyGenerator.infrastructure;

import cz.novoj.generation.contract.StandardJavaMethods;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@CommonsLog
public abstract class AbstractDispatcherInvocationHandler<T> {
	/* proxyState object unique to each proxy instance */
	protected final T proxyState;
	/* ordered list of method classifications - ie atomic features of the proxy */
	private final List<MethodClassification<?, ?,?>> methodClassifications = new LinkedList<>();

    protected AbstractDispatcherInvocationHandler(T proxyState, MethodClassification<?, ?,?>... methodClassifications) {
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

    @SuppressWarnings("unchecked")
	protected CurriedMethodContextInvocationHandler<?,?> getCurriedMethodContextInvocationHandler(Method method) {
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
