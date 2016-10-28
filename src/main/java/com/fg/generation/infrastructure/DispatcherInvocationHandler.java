package com.fg.generation.infrastructure;

import com.fg.generation.contract.StandardJavaMethods;
import javassist.util.proxy.MethodHandler;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@CommonsLog
public class DispatcherInvocationHandler<T> implements InvocationHandler, MethodHandler {
    private final T proxyState;
    private final LinkedList<MethodClassification> methodClassifications;

    /* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, ContextWiseMethodInvocationHandler> classificationCache = new ConcurrentHashMap<>(32);

    public DispatcherInvocationHandler(T proxyState, List<MethodClassification> methodClassifications) {
        this.proxyState = proxyState;
        this.methodClassifications = new LinkedList<>(methodClassifications);
        this.methodClassifications.addFirst(StandardJavaMethods.hashCodeMethodInvoker());
        this.methodClassifications.addFirst(StandardJavaMethods.equalsMethodInvoker());
        this.methodClassifications.addFirst(StandardJavaMethods.toStringMethodInvoker());
        this.methodClassifications.addFirst(StandardJavaMethods.defaultMethodInvoker());
        this.methodClassifications.addFirst(Proxy.getProxyStateMethodInvoker());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final ContextWiseMethodInvocationHandler invocationHandler =
                classificationCache.computeIfAbsent(
                        method,
                        this::getContextWiseMethodInvocationHandler
                );

        return invocationHandler.invoke(proxy, method, args, proxyState);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        final ContextWiseMethodInvocationHandler invocationHandler =
                classificationCache.computeIfAbsent(
                        thisMethod,
                        this::getContextWiseMethodInvocationHandler
                );

        return invocationHandler.invoke(self, thisMethod, args, proxyState);
    }

    private ContextWiseMethodInvocationHandler getContextWiseMethodInvocationHandler(Method method) {
        log.info("Creating proxy method handler for " + method.toGenericString());
        return methodClassifications.stream()
                .filter(methodClassification -> methodClassification.matches(method))
                .map(methodClassification -> methodClassification.createContextInvocationHandler(method))
                .findFirst()
                .orElse(StandardJavaMethods.missingImplementationInvokerWithContext());
    }

}
