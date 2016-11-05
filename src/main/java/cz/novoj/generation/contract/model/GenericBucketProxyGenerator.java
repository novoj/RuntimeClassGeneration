package cz.novoj.generation.contract.model;

import cz.novoj.generation.contract.model.dto.GenericBucket;
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

import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface GenericBucketProxyGenerator {
    String GET = "get";
    String SET = "set";
    String GET_PROPERTY = "getProperty";
    String SET_PROPERTY = "setProperty";
    String GET_PROPERTIES = "getProperties";

    static MethodClassification<String, GenericBucket, PropertyAccessor> getterInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET) && method.getParameterCount() == 0,
        /* methodContext */ method -> uncapitalize(method.getName().substring(GET.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.get(methodContext)
        );
    }

    static MethodClassification<String, GenericBucket, PropertyAccessor> setterInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(SET) && method.getParameterCount() == 1,
        /* methodContext */ method -> uncapitalize(method.getName().substring(SET.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.put(methodContext, args[0])
        );
    }

    static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertiesInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(PropertyAccessor.class.getDeclaredMethod(GET_PROPERTIES)),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
        );
    }

    static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertyInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(PropertyAccessor.class.getDeclaredMethod(GET_PROPERTY, String.class)),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.get(args[0])
        );
    }

    static MethodClassification<Void, GenericBucket, PropertyAccessor> setPropertyInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(PropertyAccessor.class.getDeclaredMethod(SET_PROPERTY, String.class, Object.class)),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.put(String.valueOf(args[0]), args[1])
        );
    }

    static <T> T instantiateJdkProxy(Class<T> contract) {
        return JdkProxyGenerator.instantiate(
                new JdkProxyDispatcherInvocationHandler<>(
                        new GenericBucket(64),
                        getPropertiesInvoker(),
                        getPropertyInvoker(),
                        setPropertyInvoker(),
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
                        new GenericBucket(64),
                        getPropertiesInvoker(),
                        getPropertyInvoker(),
                        setPropertyInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JavassistDeserializationProxyFactory.INSTANCE)
                ),
                contract, SerializableProxy.class);
    }

    static <T> T instantiateByteBuddyProxy(Class<T> contract) {
        return ByteBuddyProxyGenerator.instantiate(
                new ByteBuddyDispatcherInvocationHandler<>(
                        new GenericBucket(64),
                        getPropertiesInvoker(),
                        getPropertyInvoker(),
                        setPropertyInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(ByteBuddyDeserializationProxyFactory.INSTANCE)
                ),
                contract, SerializableProxy.class);
    }

}
