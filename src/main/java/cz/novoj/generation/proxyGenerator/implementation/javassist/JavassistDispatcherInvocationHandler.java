package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.proxyGenerator.infrastructure.AbstractDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import javassist.util.proxy.MethodHandler;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CommonsLog
public class JavassistDispatcherInvocationHandler<T> extends AbstractDispatcherInvocationHandler<T> implements MethodHandler {

    /* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, CurriedMethodContextInvocationHandler<?,?>> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);

    public JavassistDispatcherInvocationHandler(T proxyState, MethodClassification<?, ?,?>... methodClassifications) {
        super(proxyState, methodClassifications);
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

}
