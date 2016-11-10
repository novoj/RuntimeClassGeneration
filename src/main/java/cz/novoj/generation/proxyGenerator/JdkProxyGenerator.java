package cz.novoj.generation.proxyGenerator;

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
public final class JdkProxyGenerator {
    private static final Map<List<Class>, Class> cachedProxyClasses = new ConcurrentHashMap<>(64);
    private static final Map<Class, Constructor> cachedProxyConstructors = new ConcurrentHashMap<>(64);

    public static <T> T instantiate(InvocationHandler invocationHandler, Class... interfaces) {
        return instantiateProxy(
                getProxyClass(Arrays.asList(interfaces)),
                invocationHandler
        );
    }

	@SuppressWarnings("unchecked")
	private static <T> T instantiateProxy(Class proxyClass, InvocationHandler invocationHandler) {
		try {
			Constructor constructor = cachedProxyConstructors.computeIfAbsent(
					proxyClass, JdkProxyGenerator::findParametrizedConstructor
			);
			return (T) constructor.newInstance(invocationHandler);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(
					"What the heck? Can't create proxy: " + e.getMessage(), e
			);
		}
	}

    private static Class getProxyClass(List<Class> contract) {
        return cachedProxyClasses.computeIfAbsent(
                contract,
				JdkProxyGenerator::constructClass
		);
    }

	private static Class constructClass(List<Class> contract) {
		final Class[] interfaces = new Class[contract.size() + 1];
		interfaces[0] = cz.novoj.generation.proxyGenerator.infrastructure.Proxy.class;
		System.arraycopy(contract.toArray(), 0, interfaces, 1, contract.size());

		Class<?> proxyClass = Proxy.getProxyClass(JdkProxyGenerator.class.getClassLoader(), interfaces);
		log.info("Created proxy class: " + proxyClass.getName());
		return proxyClass;
	}

	@SuppressWarnings("unchecked")
	private static Constructor findParametrizedConstructor(Class proxyClass) {
		try {
			return proxyClass.getConstructor(InvocationHandler.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
				"What the heck? Can't find proper constructor on proxy: " + e.getMessage(), e
			);
		}
	}

}
