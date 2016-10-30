package com.fg.generation.jdkProxy;

import com.fg.generation.infrastructure.AbstractDispatcherInvocationHandler;
import com.fg.generation.infrastructure.ContextWiseMethodInvocationHandler;
import com.fg.generation.infrastructure.MethodClassification;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
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
