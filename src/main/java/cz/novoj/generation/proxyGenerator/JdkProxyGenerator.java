package cz.novoj.generation.proxyGenerator;

import cz.novoj.generation.model.ProxyStateAccessor;
import lombok.extern.apachecommons.CommonsLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SuppressWarnings("rawtypes")
@CommonsLog
public class JdkProxyGenerator {
    private static final Map<List<Class<?>>, Class<?>> CACHED_PROXY_CLASSES = new ConcurrentHashMap<>(64);
    private static final Map<Class<?>, Constructor<?>> CACHED_PROXY_CONSTRUCTORS = new ConcurrentHashMap<>(64);

    @SuppressWarnings("unchecked")
	public static <T> T instantiate(InvocationHandler invocationHandler, Class<?>... interfaces) {
        return instantiateProxy(
				(Class<T>)getProxyClass(Arrays.asList(interfaces)),
                invocationHandler
        );
    }

	private static Class getProxyClass(List<Class<?>> contract) {
		// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
		return CACHED_PROXY_CLASSES.computeIfAbsent(
				// CACHE KEY
				contract,
				// LAMBDA THAT FINDS OUT MISSING CONSTRUCTOR
				JdkProxyGenerator::constructClass
		);
	}

	private static Class<?> constructClass(List<Class<?>> contract) {
		// AUTOMATICALLY ADD PROXYSTATEACCESSOR CLASS TO EVERY OUR PROXY WE CREATE
		final Class<?>[] interfaces = new Class[contract.size() + 1];
		interfaces[0] = ProxyStateAccessor.class;
		System.arraycopy(contract.toArray(), 0, interfaces, 1, contract.size());

		Class<?> proxyClass = Proxy.getProxyClass(JdkProxyGenerator.class.getClassLoader(), interfaces);
		log.info("Created proxy class: " + proxyClass.getName());
		return proxyClass;
	}

	@SuppressWarnings("unchecked")
	private static <T> T instantiateProxy(Class<T> proxyClass, InvocationHandler invocationHandler) {
		try {
			// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
			Constructor<T> constructor = (Constructor<T>)CACHED_PROXY_CONSTRUCTORS.computeIfAbsent(
					// CACHE KEY
					proxyClass,
					// LAMBDA THAT FINDS OUT MISSING CONSTRUCTOR
					JdkProxyGenerator::findParametrizedConstructor
			);
			return constructor.newInstance(invocationHandler);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(
					"What the heck? Can't create proxy: " + e.getMessage(), e
			);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> findParametrizedConstructor(Class<T> proxyClass) {
		try {
			return proxyClass.getConstructor(InvocationHandler.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"What the heck? Can't find proper constructor on proxy: " + e.getMessage(), e
			);
		}
	}

}
