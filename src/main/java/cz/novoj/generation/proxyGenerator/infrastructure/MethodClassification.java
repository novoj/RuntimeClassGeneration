package cz.novoj.generation.proxyGenerator.infrastructure;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@RequiredArgsConstructor
public class MethodClassification<T, S, U> {
    public static final Function<Method, Void> NO_CONTEXT = method -> null;
    private final Predicate<Method> methodMatcher;
    private final Function<Method, T> methodClassificationContext;
    private final MethodInvocationHandler<T, S, U> invocationHandler;

    public boolean matches(Method method) {
        return methodMatcher.test(method);
    }

    public ContextWiseMethodInvocationHandler<S, U> createContextInvocationHandler(Method classificationMethod) {
        return (proxy, executionMethod, args, proxyState) -> invocationHandler.invoke(
                proxy, executionMethod, args, methodClassificationContext.apply(classificationMethod), proxyState
        );
    }

}
