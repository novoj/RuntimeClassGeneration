package cz.novoj.generation.contract.dao;

import cz.novoj.generation.contract.dao.executor.AddDaoMethodExecutor;
import cz.novoj.generation.contract.dao.executor.GetDaoMethodExecutor;
import cz.novoj.generation.contract.dao.executor.RemoveDaoMethodExecutor;
import cz.novoj.generation.contract.model.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibProxyGenerator;
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
public interface GenericBucketDaoProxyGenerator {
    String ADD = "add";
    String GET = "get";
    String REMOVE = "remove";

    static <T extends PropertyAccessor> MethodClassification<AddDaoMethodExecutor<T>, GenericBucketRepository<T>, Dao<T>> addInvoker(Class<T> itemClass) {
        return new MethodClassification<>(
        /* matcher */       method -> ADD.equals(method.getName()) && (method.getParameterCount() > 1 ||
                (method.getParameterCount() == 1 && !itemClass.isAssignableFrom(method.getParameterTypes()[0]))),
        /* methodContext */ AddDaoMethodExecutor::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
            T item = methodContext.apply(proxy.createNew(), args);
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

    static <T extends PropertyAccessor> MethodClassification<GetDaoMethodExecutor<T>, GenericBucketRepository<T>, Dao<T>> getInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET),
        /* methodContext */ GetDaoMethodExecutor::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> methodContext.apply(proxyState, args));
    }

    static <T extends PropertyAccessor> MethodClassification<RemoveDaoMethodExecutor<T>, GenericBucketRepository<T>, Dao<T>> removeInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(REMOVE),
        /* methodContext */ RemoveDaoMethodExecutor::new,
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

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateCglibProxy(Class<T> daoClass, Class<S> itemClass) {
        return CglibProxyGenerator.instantiate(
                new CglibDispatcherInvocationHandler<>(
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
