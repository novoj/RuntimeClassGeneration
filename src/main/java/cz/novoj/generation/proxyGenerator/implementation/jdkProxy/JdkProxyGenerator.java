package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@SuppressWarnings("rawtypes")
@CommonsLog
public class JdkProxyGenerator {
    private static final Map<List<Class>, Class> CACHED_PROXY_CLASSES = new ConcurrentHashMap<>(64);
    private static final Map<Class, Constructor> CACHED_PROXY_CONSTRUCTORS = new ConcurrentHashMap<>(64);

	private JdkProxyGenerator() {}

	public static <T> T instantiate(InvocationHandler invocationHandler, Class... interfaces) {
        return instantiateProxy(
                getProxyClass(interfaces),
                invocationHandler
        );
    }

    private static Class getProxyClass(Class... contract) {
        return CACHED_PROXY_CLASSES.computeIfAbsent(
                Arrays.asList(contract),
                classes -> {
                    Class[] interfaces = new Class[contract.length + 1];
                    interfaces[0] = cz.novoj.generation.contract.Proxy.class;
                    System.arraycopy(contract, 0, interfaces, 1, contract.length);

                    Class<?> proxyClass = Proxy.getProxyClass(JdkProxyGenerator.class.getClassLoader(), interfaces);
                    log.info("Created proxy class: " + proxyClass.getName());
                    return proxyClass;
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class proxyClass, InvocationHandler invocationHandler) {
        try {
            Constructor constructor = CACHED_PROXY_CONSTRUCTORS.computeIfAbsent(
                    proxyClass, aClass -> {
                        try {
                            return proxyClass.getConstructor(InvocationHandler.class);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException("What the heck? Can't find proper constructor on proxy: " + e.getMessage(), e);
                        }
                    }
            );
            return (T) constructor.newInstance(invocationHandler);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
    }

}
