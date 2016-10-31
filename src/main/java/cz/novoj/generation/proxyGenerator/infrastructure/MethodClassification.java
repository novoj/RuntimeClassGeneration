package cz.novoj.generation.proxyGenerator.infrastructure;

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
        try {
            return methodMatcher.matches(method);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Matcher " + methodMatcher.getClass().getName() +
                    " failed to process " + method.toGenericString() + ": " + ex.getMessage(), ex
            );
        }
    }

    public ContextWiseMethodInvocationHandler<S> createContextInvocationHandler(Method classificationMethod) {
        return (proxy, executionMethod, args, proxyState) -> invocationHandler.invoke(
                proxy, executionMethod, args, methodClassificationContext.apply(classificationMethod), proxyState
        );
    }

}