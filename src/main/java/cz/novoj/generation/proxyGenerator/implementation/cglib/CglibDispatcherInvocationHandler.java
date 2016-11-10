package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.proxyGenerator.infrastructure.AbstractDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.ContextWiseMethodInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@CommonsLog
public class CglibDispatcherInvocationHandler<T> extends AbstractDispatcherInvocationHandler<T> implements MethodInterceptor {

    /* this cache might be somewhere else, but for the sake of the example ... */
    private static final Map<Method, ContextWiseMethodInvocationHandler> CLASSIFICATION_CACHE = new ConcurrentHashMap<>(32);

    public CglibDispatcherInvocationHandler(T proxyState, MethodClassification... methodClassifications) {
        super(proxyState, methodClassifications);
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        final ContextWiseMethodInvocationHandler invocationHandler =
                CLASSIFICATION_CACHE.computeIfAbsent(
                        method,
                        this::getContextWiseMethodInvocationHandler
                );

        return invocationHandler.invoke(o, method, objects, proxyState);
    }

}
