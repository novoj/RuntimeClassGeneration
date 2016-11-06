package cz.novoj.generation.contract.dao;

import cz.novoj.generation.contract.dao.executor.AddMethodExecutor;
import cz.novoj.generation.contract.dao.executor.GetMethodExecutor;
import cz.novoj.generation.contract.dao.executor.RemoveMethodExecutor;
import cz.novoj.generation.contract.dao.repository.GenericBucketRepository;
import cz.novoj.generation.contract.model.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.NO_CONTEXT;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface DaoProxyGenerator {
    String ADD = "add";
    String GET = "get";
    String REMOVE = "remove";

    static <T extends PropertyAccessor> MethodClassification<AddMethodExecutor, GenericBucketRepository<T>, Dao<T>> addInvoker(Class<T> itemClass) {
        return new MethodClassification<>(
        /* matcher */       method -> ADD.equals(method.getName()) && (method.getParameterCount() > 1 ||
                (method.getParameterCount() == 1 && !itemClass.isAssignableFrom(method.getParameterTypes()[0]))),
        /* methodContext */ AddMethodExecutor::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
            T item = methodContext.populate(proxy.createNew(), args);
            proxyState.add(item);
            return null;
        });
    }

    static <T extends PropertyAccessor> MethodClassification<Void, GenericBucketRepository<T>, Dao<T>> addProxyInvoker(Class<T> itemClass) {
        return new MethodClassification<>(
        /* matcher */       method -> ADD.equals(method.getName()) &&
                (method.getParameterCount() == 1 && itemClass.isAssignableFrom(method.getParameterTypes()[0])),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
            proxyState.add((T) args[0]);
            return null;
        });
    }

    static <T extends PropertyAccessor> MethodClassification<GetMethodExecutor<T>, GenericBucketRepository<T>, Dao<T>> getInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET),
        /* methodContext */ GetMethodExecutor::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> methodContext.apply(proxyState, args));
    }

    static <T extends PropertyAccessor> MethodClassification<RemoveMethodExecutor<T>, GenericBucketRepository<T>, Dao<T>> removeInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(REMOVE),
        /* methodContext */ RemoveMethodExecutor::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> methodContext.apply(proxyState, args));
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateJdkProxy(Class<T> daoClass, Class<S> itemClass) {
        return JdkProxyGenerator.instantiate(
                new JdkProxyDispatcherInvocationHandler<>(
                        new GenericBucketRepository<S>(),
                        getInvoker(),
                        addInvoker(itemClass),
                        addProxyInvoker(itemClass),
                        removeInvoker()
                ),
                daoClass, SerializableProxy.class
        );
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateJavassistProxy(Class<T> daoClass, Class<S> itemClass) {
        return JavassistProxyGenerator.instantiate(
                new JavassistDispatcherInvocationHandler<>(
                        new GenericBucketRepository<S>(),
                        getInvoker(),
                        addInvoker(itemClass),
                        addProxyInvoker(itemClass),
                        removeInvoker()
                ),
                daoClass, SerializableProxy.class);
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateByteBuddyProxy(Class<T> daoClass, Class<S> itemClass) {
        return ByteBuddyProxyGenerator.instantiate(
                new ByteBuddyDispatcherInvocationHandler<>(
                        new GenericBucketRepository<S>(),
                        getInvoker(),
                        addInvoker(itemClass),
                        addProxyInvoker(itemClass),
                        removeInvoker()
                ),
                daoClass, SerializableProxy.class);
    }

}
