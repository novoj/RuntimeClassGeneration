package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import cz.novoj.generation.proxyGenerator.infrastructure.AbstractDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodCall;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CommonsLog
public class JdkProxyDispatcherInvocationHandler<T> extends AbstractDispatcherInvocationHandler<T> implements InvocationHandler {

	/* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, CurriedMethodContextInvocationHandler<?,?>> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);

	public JdkProxyDispatcherInvocationHandler(T proxyState, MethodClassification<?, ?, ?>... methodClassifications) {
        super(proxyState, methodClassifications);
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
		final MethodCall methodCall = new JdkProxyMethodCall(proxy, method, args);
		return invocationHandler.invoke(methodCall, proxy, args, proxyState);
    }

}
