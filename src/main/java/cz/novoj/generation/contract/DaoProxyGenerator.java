package cz.novoj.generation.contract;

import cz.novoj.generation.contract.helper.PropertyPopulator;
import cz.novoj.generation.dao.Dao;
import cz.novoj.generation.dao.GenericBucketRepository;
import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface DaoProxyGenerator {
    String GET_ALL = "getAll";
    String ADD = "add";
    String GET = "get";

    static <T extends PropertyAccessor> MethodClassification<Void, GenericBucketRepository<T>, Dao<T>> getAllInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> GET_ALL.equals(method.getName()) && method.getParameterCount() == 0,
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.getData()
        );
    }

    static <T extends PropertyAccessor> MethodClassification<PropertyPopulator, GenericBucketRepository<T>, Dao<T>> addInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> ADD.equals(method.getName()),
        /* methodContext */ PropertyPopulator::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> methodContext.populate(proxy.createNew(), args));
    }

    static <T extends PropertyAccessor> MethodClassification<String, GenericBucketRepository<T>, Dao<T>> getByInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET) && method.getParameterCount() == 0,
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.getData().stream().findFirst().orElse(null)
        );
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateJdkProxy(Class<T> contract) {
        return JdkProxyGenerator.instantiate(
                new JdkProxyDispatcherInvocationHandler<>(
                        new GenericBucketRepository<S>(),
                        getAllInvoker(),
                        getByInvoker(),
                        addInvoker()
                ),
                contract, SerializableProxy.class
        );
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateJavassistProxy(Class<T> contract) {
        return JavassistProxyGenerator.instantiate(
                new JavassistDispatcherInvocationHandler<>(
                        new GenericBucketRepository<S>(),
                        getAllInvoker(),
                        getByInvoker(),
                        addInvoker()
                ),
                contract, SerializableProxy.class);
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateByteBuddyProxy(Class<T> contract) {
        return ByteBuddyProxyGenerator.instantiate(
                new ByteBuddyDispatcherInvocationHandler<>(
                        new GenericBucketRepository<S>(),
                        getAllInvoker(),
                        getByInvoker(),
                        addInvoker()
                ),
                contract, SerializableProxy.class);
    }

}
