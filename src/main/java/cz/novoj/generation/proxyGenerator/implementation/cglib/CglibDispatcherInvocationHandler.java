package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.proxyGenerator.infrastructure.AbstractDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CommonsLog
public class CglibDispatcherInvocationHandler<T> extends AbstractDispatcherInvocationHandler<T> implements MethodInterceptor {

    /* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, CurriedMethodContextInvocationHandler<?,?>> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);

    public CglibDispatcherInvocationHandler(T proxyState, MethodClassification<?, ?,?>... methodClassifications) {
        super(proxyState, methodClassifications);
    }

	@SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
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

}
