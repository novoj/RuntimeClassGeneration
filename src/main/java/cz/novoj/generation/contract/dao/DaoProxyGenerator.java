package cz.novoj.generation.contract.dao;

import cz.novoj.generation.contract.dao.dto.GenericBucketRepository;
import cz.novoj.generation.contract.dao.helper.MethodNameDecomposition;
import cz.novoj.generation.contract.dao.helper.PropertyPopulator;
import cz.novoj.generation.contract.model.PropertyAccessor;
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
    String ADD = "add";
    String GET = "get";
    String GET_BY = "getBy";
    String GET_ALL = "getAll";

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
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
            T item = methodContext.populate(proxy.createNew(), args);
            proxyState.add(item);
            return null;
        });
    }

    static <T extends PropertyAccessor> MethodClassification<MethodNameDecomposition<T>, GenericBucketRepository<T>, Dao<T>> getByInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET_BY),
        /* methodContext */ MethodNameDecomposition::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> methodContext.apply(proxyState, args));
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
