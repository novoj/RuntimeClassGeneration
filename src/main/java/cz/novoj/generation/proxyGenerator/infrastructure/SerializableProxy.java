package cz.novoj.generation.proxyGenerator.infrastructure;

import javassist.util.proxy.ProxyObject;
import lombok.extern.apachecommons.CommonsLog;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.*;

import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public interface SerializableProxy extends Serializable {
    Set<Class> EXCLUDED_CLASSES = new HashSet<>(
            Arrays.asList(
                    Proxy.class,
                    ProxyObject.class,
                    ProxyStateAccessor.class
            )
    );

    Object writeReplace() throws ObjectStreamException;

    static <T> MethodClassification<ProxyStateAccessor, Void, T> getWriteReplaceMethodInvoker(DeserializationProxyFactory<T> deserializationProxyFactory) {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, SerializableProxy.class, "writeReplace"),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
            final Object state = proxy.getProxyState();
            final Class superclass = proxy.getClass().getSuperclass();
            final Class[] interfaces = proxy.getClass().getInterfaces();
            final Class[] allInterfaces = combineInterfaces(superclass, interfaces);
            return new SerializableProxyDescriptor(
                    allInterfaces, state, deserializationProxyFactory
            );
        });
    }

    static Class[] combineInterfaces(Class superclass, Class[] interfaces) {
        final List<Class> combined = new LinkedList<>();
        if (!Object.class.equals(superclass) && !EXCLUDED_CLASSES.contains(superclass)) {
            combined.add(superclass);
        }
        for (Class anInterface : interfaces) {
            if (!EXCLUDED_CLASSES.contains(anInterface)) {
                combined.add(anInterface);
            }
        }
        return combined.toArray(new Class[combined.size()]);
    }

    @CommonsLog
    class SerializableProxyDescriptor<T> implements Serializable {
        private static final long serialVersionUID = 8401525823871149500L;
        private final Class<?>[] interfaces;
        private final T target;
        private final DeserializationProxyFactory<T> deserializationProxyFactory;

        private SerializableProxyDescriptor(Class<?>[] interfaces, T target, DeserializationProxyFactory<T> deserializationProxyFactory) {
            this.interfaces = interfaces;
            this.target = target;
            this.deserializationProxyFactory = deserializationProxyFactory;
        }

        protected Object readResolve() throws ObjectStreamException {
            return deserializationProxyFactory.deserialize(target, interfaces);
        }

    }

    interface DeserializationProxyFactory<T> extends Serializable {

        Object deserialize(T target, Class<?>[] interfaces);

    }

}
