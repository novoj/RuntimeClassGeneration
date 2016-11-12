package cz.novoj.generation.contract.dao;

import cz.novoj.generation.contract.dao.executor.AddDaoMethodExecutor;
import cz.novoj.generation.contract.dao.executor.GetDaoMethodExecutor;
import cz.novoj.generation.contract.dao.executor.RemoveDaoMethodExecutor;
import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import cz.novoj.generation.contract.model.SerializableProxy;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.NO_CONTEXT;

public interface GenericBucketDaoProxyGenerator {
    String ADD = "add";
    String GET = "get";
    String REMOVE = "remove";

	/** METHOD CONTRACT: void add(T item) **/
	static <T extends PropertyAccessor> MethodClassification<Dao<T>, Void, GenericBucketRepository<T>> addProxyInvoker(Class<T> itemClass) {
		return new MethodClassification<>(
        /* matcher */       method -> ADD.equals(method.getName()) &&
				(method.getParameterCount() == 1 && itemClass.isAssignableFrom(method.getParameterTypes()[0])),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
			proxyState.add((T) args[0]);
			return null;
		});
	}

	/** METHOD CONTRACT: void add(Object propertyA, Object propertyB) **/
    static <T extends PropertyAccessor> MethodClassification<Dao<T>, AddDaoMethodExecutor<T>, GenericBucketRepository<T>> addInvoker(Class<T> itemClass) {
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

	/** METHOD CONTRACT: SpecificResult getByFilterSortedBySort(Object paramA, Object paramB) **/
    static <T extends PropertyAccessor> MethodClassification<Dao<T>, GetDaoMethodExecutor<T>, GenericBucketRepository<T>> getInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET),
        /* methodContext */ GetDaoMethodExecutor::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> methodContext.apply(proxyState, args));
    }

	/** METHOD CONTRACT: SpecificResult removeByFilter(Object paramA, Object paramB) **/
    static <T extends PropertyAccessor> MethodClassification<Dao<T>, RemoveDaoMethodExecutor<T>, GenericBucketRepository<T>> removeInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(REMOVE),
        /* methodContext */ RemoveDaoMethodExecutor::new,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> methodContext.apply(proxyState, args));
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateJdkProxy(Class<T> daoClass, Class<S> itemClass) {
        return JdkProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
                new JdkProxyDispatcherInvocationHandler<>(
						// proxy state
                        new GenericBucketRepository<S>(),
						// list of features - order is important
                        getInvoker(),
                        addInvoker(itemClass),
                        addProxyInvoker(itemClass),
                        removeInvoker()
                ),
				// interfaces to implement
                daoClass, SerializableProxy.class
        );
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateJavassistProxy(Class<T> daoClass, Class<S> itemClass) {
        return JavassistProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
                new JavassistDispatcherInvocationHandler<>(
						// proxy state
                        new GenericBucketRepository<S>(),
						// list of features - order is important
                        getInvoker(),
                        addInvoker(itemClass),
                        addProxyInvoker(itemClass),
                        removeInvoker()
                ),
				// interfaces to implement
                daoClass, SerializableProxy.class);
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateCglibProxy(Class<T> daoClass, Class<S> itemClass) {
        return CglibProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
                new CglibDispatcherInvocationHandler<>(
						// proxy state
                        new GenericBucketRepository<S>(),
						// list of features - order is important
                        getInvoker(),
                        addInvoker(itemClass),
                        addProxyInvoker(itemClass),
                        removeInvoker()
                ),
				// interfaces to implement
                daoClass, SerializableProxy.class);
    }

    static <T extends Dao<S>, S extends PropertyAccessor> T instantiateByteBuddyProxy(Class<T> daoClass, Class<S> itemClass) {
        return ByteBuddyProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
                new ByteBuddyDispatcherInvocationHandler<>(
						// proxy state
                        new GenericBucketRepository<S>(),
						// list of features - order is important
                        getInvoker(),
                        addInvoker(itemClass),
                        addProxyInvoker(itemClass),
                        removeInvoker()
                ),
				// interfaces to implement
                daoClass, SerializableProxy.class);
    }

}
