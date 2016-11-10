package cz.novoj.generation.proxyGenerator.implementation.javassist;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.apachecommons.CommonsLog;

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
public final class JavassistProxyGenerator {
    private static Map<List<Class>, Class> cachedProxyClasses = new ConcurrentHashMap<>(64);

    public static <T> T instantiate(MethodHandler methodHandler, Class... interfaces) {
        return instantiateProxy(
                getProxyClass(interfaces),
                methodHandler
        );
    }

    private static Class getProxyClass(Class... interfaces) {
        return cachedProxyClasses.computeIfAbsent(
                Arrays.asList(interfaces),
                classes -> {
                    ProxyFactory f = new ProxyFactory();
                    if (interfaces[0].isInterface()) {
                        final Class[] finalContract = new Class[interfaces.length + 1];
                        finalContract[0] = cz.novoj.generation.contract.Proxy.class;
                        System.arraycopy(interfaces, 0, finalContract, 1, interfaces.length);

                        f.setInterfaces(finalContract);
                    } else {
                        final Class[] finalContract = new Class[interfaces.length];
                        finalContract[0] = cz.novoj.generation.contract.Proxy.class;
                        System.arraycopy(interfaces, 1, finalContract, 1, interfaces.length - 1);

                        f.setSuperclass(interfaces[0]);
                        f.setInterfaces(finalContract);
                    }

                    f.setFilter(m -> !java.util.Objects.equals(m.getName(), "finalize"));

                    Class proxyClass = f.createClass();
                    log.info("Created proxy class: " + proxyClass.getName());
                    return proxyClass;
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class proxyClass, MethodHandler methodHandler) {
        try {
            T proxy = (T) proxyClass.newInstance();
            ((Proxy) proxy).setHandler(methodHandler);

            return proxy;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
    }

}
