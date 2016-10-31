package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.proxyGenerator.infrastructure.AbstractDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.ContextWiseMethodInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import javassist.util.proxy.MethodHandler;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@CommonsLog
public class JavassistDispatcherInvocationHandler<T> extends AbstractDispatcherInvocationHandler<T> implements MethodHandler {

    /* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, ContextWiseMethodInvocationHandler> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);

    public JavassistDispatcherInvocationHandler(T proxyState, MethodClassification... methodClassifications) {
        super(proxyState, methodClassifications);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        final ContextWiseMethodInvocationHandler invocationHandler =
                CLASSIFICATION_CACHE.computeIfAbsent(
                        thisMethod,
                        this::getContextWiseMethodInvocationHandler
                );

        return invocationHandler.invoke(self, proceed == null ? thisMethod : proceed, args, proxyState);
    }

}
