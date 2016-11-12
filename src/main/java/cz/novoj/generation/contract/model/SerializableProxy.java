package cz.novoj.generation.contract.model;

import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import javassist.util.proxy.ProxyObject;
import lombok.extern.apachecommons.CommonsLog;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.NO_CONTEXT;
import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;

public interface SerializableProxy extends Serializable {
	// LIST OF "SYSTEM" INTERFACES THAT ARE ADDED TO OUR PROXIES AUTOMATICALLY EITHER BY US OR BY THE BYTECODE LIBRARY
    Set<Class<?>> EXCLUDED_CLASSES = new HashSet<>(
            Arrays.asList(
                    Proxy.class,
                    ProxyObject.class,
                    ProxyStateAccessor.class
            )
    );

	/**
	 * This method will be called
	 * @return
	 * @throws ObjectStreamException
	 */
	Object writeReplace() throws ObjectStreamException;

    static <T> MethodClassification<ProxyStateAccessor, Void, T> getWriteReplaceMethodInvoker(DeserializationProxyFactory<T> deserializationProxyFactory) {
        return new MethodClassification<>(
		/* matcher */       method -> isMethodDeclaredOn(method, SerializableProxy.class, "writeReplace"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) ->
									new SerializableProxyDescriptor<T>(
										deserializationProxyFactory,
										proxyState,
										combineInterfaces(
											proxy.getClass().getSuperclass(),
											proxy.getClass().getInterfaces()
										)
									)
		);
    }

	/**
	 * Combines superclass with interfaces into single list excluding "system" interfaces that are added to the proxy
	 * automatically.
	 *
	 * @param superclass
	 * @param interfaces
	 * @return
	 */
    static Class<?>[] combineInterfaces(Class<?> superclass, Class<?>[] interfaces) {
        final List<Class<?>> combined = new LinkedList<>();
        if (!Object.class.equals(superclass) && !EXCLUDED_CLASSES.contains(superclass)) {
            combined.add(superclass);
        }
        for (Class<?> anInterface : interfaces) {
            if (!EXCLUDED_CLASSES.contains(anInterface)) {
                combined.add(anInterface);
            }
        }
        return combined.toArray(new Class[combined.size()]);
    }

	/**
	 * Recipe how to recreate proxy class and its instance on deserialization. Contains all mandatory informations
	 * to create identical proxy on deserialization.
	 *
	 * @param <T>
	 */
	@CommonsLog
    class SerializableProxyDescriptor<T> implements Serializable {
        private static final long serialVersionUID = 8401525823871149500L;
        private final Class<?>[] interfaces;
        private final T target;
        private final DeserializationProxyFactory<T> deserializationProxyFactory;

        private SerializableProxyDescriptor(DeserializationProxyFactory<T> deserializationProxyFactory, T target, Class<?>... interfaces) {
            this.interfaces = interfaces;
            this.target = target;
            this.deserializationProxyFactory = deserializationProxyFactory;
        }

		/**
		 * This method will be called by JDK to deserialize object.
		 * @return
		 * @throws ObjectStreamException
		 */
		protected Object readResolve() throws ObjectStreamException {
            return deserializationProxyFactory.deserialize(target, interfaces);
        }

    }

	/**
	 * Logic that will deserialize {@link SerializableProxyDescriptor} back to the proxy class and instance.
	 * We need this only because we want to support several byte code generation libraries here in this example.
	 *
	 * @param <T>
	 */
	interface DeserializationProxyFactory<T> extends Serializable {

        Object deserialize(T target, Class<?>[] interfaces);

    }

}
