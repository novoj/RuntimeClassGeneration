package cz.novoj.generation.contract.dao;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.JavassistDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.JavassistProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.NO_CONTEXT;

public interface GenericBucketDaoProxyGenerator {
	String ADD = "add";
	String GET_ALL = "getAll";
	String CREATE_NEW = "createNew";

	/** METHOD CONTRACT: void add(Object propertyA, Object propertyB) **/
	static <T extends PropertyAccessor> MethodClassification<Dao<T>, AddDaoMethodExecutor<T>, GenericBucketRepository<T>> addByParametersInvoker(Class<T> itemClass) {
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

	/** METHOD CONTRACT: List<T> getAll() **/
	static <T extends PropertyAccessor> MethodClassification<Dao<T>, Void, GenericBucketRepository<T>> getAllInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> GET_ALL.equals(method.getName()),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.getData());
	}

	/** METHOD CONTRACT: T createNew() **/
	static <T extends PropertyAccessor> MethodClassification<Dao<T>, Void, GenericBucketRepository<T>> createNewProxyInvoker(Class<T> itemClass) {
		return new MethodClassification<>(
        /* matcher */       method -> CREATE_NEW.equals(method.getName()),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> GenericBucketProxyGenerator.instantiate(itemClass));
	}

	static <T extends Dao<S>, S extends PropertyAccessor> T instantiateJavassistProxy(Class<T> daoClass, Class<S> itemClass) {
		return JavassistProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
				new JavassistDispatcherInvocationHandler<>(
						// proxy state
						new GenericBucketRepository<S>(),
						// list of features - order is important
						addProxyInvoker(itemClass),
						addByParametersInvoker(itemClass),
						getAllInvoker(),
						createNewProxyInvoker(itemClass)
				),
				// interfaces to implement
				daoClass);
	}

}
