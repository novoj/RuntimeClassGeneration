package com.fg.generation.infrastructure;

import lombok.extern.apachecommons.CommonsLog;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public interface SerializableProxy extends Serializable {
    Set<Class> excludedInterfaces = new HashSet<>(
            Arrays.asList(
                    java.lang.reflect.Proxy.class,
                    javassist.util.proxy.ProxyObject.class,
                    Proxy.class
            )
    );

    Object writeReplace() throws ObjectStreamException;

    static <T> MethodClassification<Void, T> getWriteReplaceMethodInvoker(DeserializationProxyFactory<T> deserializationProxyFactory) {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(SerializableProxy.class.getDeclaredMethod("writeReplace")),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
            final Object state = ((Proxy) proxy).getProxyState();
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
        if (!Object.class.equals(superclass)) {
            combined.add(superclass);
        }
        for (Class anInterface : interfaces) {
            if (!excludedInterfaces.contains(anInterface)) {
                combined.add(anInterface);
            }
        }
        return combined.toArray(new Class[combined.size()]);
    }

    @CommonsLog
    class SerializableProxyDescriptor implements Serializable {
        private static final long serialVersionUID = 8401525823871149500L;
        private final Class[] interfaces;
        private final Object target;
        private final DeserializationProxyFactory deserializationProxyFactory;

        private SerializableProxyDescriptor(Class[] interfaces, Object target, DeserializationProxyFactory deserializationProxyFactory) {
            this.interfaces = interfaces;
            this.target = target;
            this.deserializationProxyFactory = deserializationProxyFactory;
        }

        public Object readResolve() throws ObjectStreamException {
            return deserializationProxyFactory.deserialize(target, interfaces);
        }

    }

    interface DeserializationProxyFactory<T> extends Serializable {

        Object deserialize(T target, Class[] interfaces);

    }

}
