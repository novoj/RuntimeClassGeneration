package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.contract.model.ProxyStateAccessor;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@CommonsLog
public class CglibProxyGenerator {
	private static final Map<List<Class<?>>, Class<?>> CACHED_PROXY_CLASSES = new ConcurrentHashMap<>(64);

	@SuppressWarnings("unchecked")
	public static <T> T instantiate(Callback invocationHandler, Class<?>... interfaces) {
		return instantiateProxy(
				(Class<T>)getProxyClass(interfaces),
				invocationHandler
		);
	}

	private static Class<?> getProxyClass(Class<?>... interfaces) {
		// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
		return CACHED_PROXY_CLASSES.computeIfAbsent(
				// CACHE KEY
				Arrays.asList(interfaces),
				// LAMBDA THAT FINDS OUT MISSING CONSTRUCTOR
				classes -> {
					final Enhancer fct = new Enhancer();

					// IF WE PROXY ABSTRACT CLASS, WE HAVE A RULE THAT IT HAS TO BE FIRST IN LIST
					if (interfaces[0].isInterface()) {
						// FIRST IS INTERFACE
						// AUTOMATICALLY ADD PROXYSTATEACCESSOR CLASS TO EVERY OUR PROXY WE CREATE
						final Class<?>[] finalContract = new Class[interfaces.length + 1];
						finalContract[0] = ProxyStateAccessor.class;
						System.arraycopy(interfaces, 0, finalContract, 1, interfaces.length);
						// WE'LL EXTEND OBJECT CLASS AND IMPLEMENT ALL INTERFACES
						fct.setInterfaces(finalContract);
					} else {
						// FIRST IS ABSTRACT CLASS
						// AUTOMATICALLY ADD PROXYSTATEACCESSOR CLASS TO EVERY OUR PROXY WE CREATE
						final Class<?>[] finalContract = new Class[interfaces.length];
						finalContract[0] = ProxyStateAccessor.class;
						System.arraycopy(interfaces, 1, finalContract, 1, interfaces.length - 1);
						// WE'LL EXTEND ABSTRACT CLASS AND IMPLEMENT ALL OTHER INTERFACES
						fct.setSuperclass(interfaces[0]);
						fct.setInterfaces(finalContract);
					}

					// SKIP FINALIZE METHOD OVERRIDE - STAY AWAY FROM TROUBLE :)
					fct.setCallbackFilter(method -> "finalize".equals(method.getName()) && method.getParameterCount() == 0 ? 0 : 1);
					// SET CALLBACK TO THE NEWLY CREATED INSTANCE
					fct.setCallbackTypes(new Class[]{NoOp.class, CglibDispatcherInvocationHandler.class});

					Class<?> proxyClass = fct.createClass();
					log.info("Created proxy class: " + proxyClass.getName());

					return proxyClass;
				});
	}

	@SuppressWarnings("unchecked")
	private static <T> T instantiateProxy(Class<T> proxyClass, Callback invocationHandler) {
		try {
			final T proxy = proxyClass.getConstructor().newInstance();
			((Factory) proxy).setCallbacks(new Callback[] {NoOp.INSTANCE, invocationHandler});
			return proxy;
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("What the heck? Can't create proxy: " + e.getMessage(), e);
		}
	}

}
