package cz.novoj.generation.proxyGenerator.implementation.cglib;

import lombok.extern.apachecommons.CommonsLog;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@CommonsLog
public final class CglibProxyGenerator {
    private static Map<List<Class>, Class> cachedProxyClasses = new ConcurrentHashMap<>(64);
    private static Map<Class, Constructor> cachedProxyConstructors = new ConcurrentHashMap<>(64);

    public static <T> T instantiate(MethodInterceptor methodHandler, Class... interfaces) {
        return instantiateProxy(
                getProxyClass(interfaces),
                methodHandler
        );
    }

    private static Class getProxyClass(Class... interfaces) {
        return cachedProxyClasses.computeIfAbsent(
                Arrays.asList(interfaces),
                classes -> {
                    Enhancer f = new Enhancer();
                    if (interfaces[0].isInterface()) {
                        final Class[] finalContract = new Class[interfaces.length + 1];
                        finalContract[0] = cz.novoj.generation.proxyGenerator.infrastructure.Proxy.class;
                        System.arraycopy(interfaces, 0, finalContract, 1, interfaces.length);

                        f.setInterfaces(finalContract);
                    } else {
                        final Class[] finalContract = new Class[interfaces.length];
                        finalContract[0] = cz.novoj.generation.proxyGenerator.infrastructure.Proxy.class;
                        System.arraycopy(interfaces, 1, finalContract, 1, interfaces.length - 1);

                        f.setSuperclass(interfaces[0]);
                        f.setInterfaces(finalContract);
                    }

                    f.setCallbackFilter(method -> "finalize".equals(method.getName()) && method.getParameterCount() == 0 ? 0 : 1);
                    f.setCallback();

                    Class proxyClass = f.createClass();
                    log.info("Created proxy class: " + proxyClass.getName());
                    return proxyClass;
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class proxyClass, MethodInterceptor methodHandler) {
        try {
            Constructor constructor = cachedProxyConstructors.computeIfAbsent(
                    proxyClass, aClass -> {
                        try {
                            return proxyClass.getConstructor(InvocationHandler.class);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException("What the heck? Can't find proper constructor on proxy: " + e.getMessage(), e);
                        }
                    }
            );
            return (T) constructor.newInstance(methodHandler);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
    }

}
