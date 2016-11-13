package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.contract.model.ProxyStateAccessor;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.apachecommons.CommonsLog;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@CommonsLog
public class JavassistProxyGenerator {
    private static final Map<List<Class<?>>, Class<?>> CACHED_PROXY_CLASSES = new ConcurrentHashMap<>(64);

    @SuppressWarnings("unchecked")
	public static <T> T instantiate(MethodHandler methodHandler, Class<?>... interfaces) {
        return instantiateProxy(
				(Class<T>)getProxyClass(interfaces),
                methodHandler
        );
    }

    private static Class<?> getProxyClass(Class<?>... interfaces) {
		// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
        return CACHED_PROXY_CLASSES.computeIfAbsent(
        		// CACHE KEY
                Arrays.asList(interfaces),
				// LAMBDA THAT CREATES OUR PROXY CLASS
                classes -> {
                    final ProxyFactory fct = new ProxyFactory();

                    // WE'LL CACHE CLASSES ON OUR OWN
                    fct.setUseCache(false);

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

					/**

					 In Effective java (2nd edition ) Joshua bloch says,

					 "Oh, and one more thing: there is a severe performance penalty for using finalizers. On my machine, the time
					 to create and destroy a simple object is about 5.6 ns.
					 Adding a finalizer increases the time to 2,400 ns. In other words, it is about 430 times slower to create and
					 destroy objects with finalizers."

					 */

                    fct.setFilter(method -> !Objects.equals(method.getName(), "finalize"));
                    // DON'T USE CACHE - WE CACHE CLASSES OURSELVES
                    fct.setUseCache(false);

                    Class<?> proxyClass = fct.createClass();
                    log.info("Created proxy class: " + proxyClass.getName());

                    return proxyClass;
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class<T> proxyClass, MethodHandler methodHandler) {
        try {

        	// CREATE PROXY INSTANCE
            T proxy = proxyClass.getConstructor().newInstance();
            // INJECT OUR METHOD HANDLER INSTANCE TO NEWLY CREATED PROXY INSTANCE
            ((Proxy) proxy).setHandler(methodHandler);

            return proxy;

        } catch (Exception e) {
            throw new IllegalArgumentException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
	}

}
