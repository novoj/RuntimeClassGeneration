package cz.novoj.generation.proxyGenerator.implementation.javassist;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@SuppressWarnings("rawtypes")
@CommonsLog
public class JavassistProxyGenerator {
    private static final Map<List<Class>, Class> CACHED_PROXY_CLASSES = new ConcurrentHashMap<>(64);

	private JavassistProxyGenerator() {}

	public static <T> T instantiate(MethodHandler methodHandler, Class... interfaces) {
        return instantiateProxy(
                getProxyClass(Arrays.asList(interfaces)),
                methodHandler
        );
    }

    private static Class getProxyClass(List<Class> interfaces) {
        return CACHED_PROXY_CLASSES.computeIfAbsent(
                interfaces,
				JavassistProxyGenerator::createClass
		);
    }

	@SuppressWarnings("SuspiciousSystemArraycopy")
	private static Class createClass(List<Class> interfaces) {
		final ProxyFactory fct = new ProxyFactory();

		if (interfaces.get(0).isInterface()) {
			/* ALL PASSED CLASSES ARE INTERFACES */

			final Class[] finalContract = new Class[interfaces.size() + 1];
			finalContract[0] = cz.novoj.generation.contract.Proxy.class;
			System.arraycopy(interfaces.toArray(), 0, finalContract, 1, interfaces.size());

			fct.setInterfaces(finalContract);

		} else {
			/* FIRST CLASS IS ABSTRACT CLASS, OTHERS ARE INTERFACES */

			final Class[] finalContract = new Class[interfaces.size()];
			finalContract[0] = cz.novoj.generation.contract.Proxy.class;
			System.arraycopy(interfaces.toArray(), 1, finalContract, 1, interfaces.size() - 1);

			fct.setSuperclass(interfaces.get(0));
			fct.setInterfaces(finalContract);

		}

		/* WE DON'T WANT TO OVERRIDE FINALIZE METHOD */

		/**

		 In Effective java (2nd edition ) Joshua bloch says,

		 "Oh, and one more thing: there is a severe performance penalty for using finalizers. On my machine, the time
		 to create and destroy a simple object is about 5.6 ns.
		 Adding a finalizer increases the time to 2,400 ns. In other words, it is about 430 times slower to create and
		 destroy objects with finalizers."

		 */

		fct.setFilter(method -> !Objects.equals(method.getName(), "finalize"));

		Class proxyClass = fct.createClass();
		log.info("Created proxy class: " + proxyClass.getName());

		return proxyClass;
	}

	@SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class proxyClass, MethodHandler methodHandler) {
        try {
            T proxy = (T)proxyClass.getConstructor().newInstance();
            ((Proxy) proxy).setHandler(methodHandler);

            return proxy;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
	}

}
