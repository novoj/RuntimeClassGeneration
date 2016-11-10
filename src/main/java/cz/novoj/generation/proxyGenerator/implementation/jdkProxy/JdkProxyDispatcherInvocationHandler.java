package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import cz.novoj.generation.proxyGenerator.implementation.AbstractDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.ContextWiseMethodInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
@CommonsLog
public class JdkProxyDispatcherInvocationHandler<T> extends AbstractDispatcherInvocationHandler<T> implements InvocationHandler {

    /* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, ContextWiseMethodInvocationHandler> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);

    public JdkProxyDispatcherInvocationHandler(T proxyState, MethodClassification... methodClassifications) {
        super(proxyState, methodClassifications);
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
