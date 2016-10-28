package com.fg.generation.jdkProxy.invocationHandler.infrastructure;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@RequiredArgsConstructor
public class MethodClassification<T, S> {
    private final MethodMatcher methodMatcher;
    private final Function<Method, T> methodClassificationContext;
    private final MethodInvocationHandler<T, S> invocationHandler;

    public boolean matches(Method method) {
        return methodMatcher.matches(method);
    }

    public ContextWiseMethodInvocationHandler<S> createContextInvocationHandler(Method method) {
        return (proxy, method1, args, proxyState) -> invocationHandler.invoke(
                proxy, method, args, methodClassificationContext.apply(method), proxyState
        );
    }

}
