package cz.novoj.generation.contract;

import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDeserializationProxyFactory;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistDeserializationProxyFactory;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyDeserializationProxyFactory;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface GenericBucketProxyGenerator {
    String GET_PREFIX = "get";
    String SET_PREFIX = "set";

    static MethodClassification<String, Map<String, Object>> getterInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET_PREFIX) && method.getParameterCount() == 0,
        /* methodContext */ method -> uncapitalize(method.getName().substring(GET_PREFIX.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.get(methodContext)
        );
    }

    static MethodClassification<String, Map<String, Object>> setterInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(SET_PREFIX) && method.getParameterCount() == 1,
        /* methodContext */ method -> uncapitalize(method.getName().substring(SET_PREFIX.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.put(methodContext, args[0])
        );
    }

    static MethodClassification<Void, Map<String, Object>> getPropertiesInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().equals("getProperties") && method.getParameterCount() == 0,
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
        );
    }

    static <T> T instantiateJdkProxy(Class<T> contract) {
        return JdkProxyGenerator.instantiate(
                new JdkProxyDispatcherInvocationHandler<>(
                        new HashMap<String, Object>(64),
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JdkProxyDeserializationProxyFactory.INSTANCE)
                ),
                contract, SerializableProxy.class
        );
    }

    static <T> T instantiateJavassistProxy(Class<T> contract) {
        return JavassistProxyGenerator.instantiate(
                new JavassistDispatcherInvocationHandler<>(
                        new HashMap<String, Object>(64),
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JavassistDeserializationProxyFactory.INSTANCE)
                ),
                contract, SerializableProxy.class);
    }

    static <T> T instantiateByteBuddyProxy(Class<T> contract) {
        return ByteBuddyProxyGenerator.instantiate(
                new ByteBuddyDispatcherInvocationHandler<>(
                        new HashMap<String, Object>(64),
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(ByteBuddyDeserializationProxyFactory.INSTANCE)
                ),
                contract, SerializableProxy.class);
    }

}
