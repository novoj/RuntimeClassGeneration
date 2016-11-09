package cz.novoj.generation.contract.model;

import cz.novoj.generation.model.Proxy;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface GenericBucketProxyGenerator {

	static <T> T instantiate(Class<T> contract) {
		return (T)java.lang.reflect.Proxy.newProxyInstance(
				GenericBucketProxyGenerator.class.getClassLoader(), new Class[] {contract, Proxy.class},
				new GenericBucketInvocationHandler()
		);
	}

	class GenericBucketInvocationHandler implements InvocationHandler {
		private final GenericBucket genericBucket = new GenericBucket(32);

		// TODO POUZE ROZDĚLENÍ NA CLASS A NEW INSTANCE

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (Proxy.class.getDeclaredMethod("getProxyState").equals(method)) {
				return genericBucket;
			} else if (method.getName().equals("getProperties")) {
				return genericBucket;
			} else if (method.getName().startsWith("get")) {
				final String propertyName = StringUtils.uncapitalize(method.getName().substring(3));
				return genericBucket.get(propertyName);
			} else if (method.getName().startsWith("set")) {
				final String propertyName = StringUtils.uncapitalize(method.getName().substring(3));
				genericBucket.put(propertyName, args[0]);
				return null;
			} else if (Object.class.getDeclaredMethod("hashCode").equals(method)) {
				return genericBucket.hashCode();
			} else if (Object.class.getDeclaredMethod("toString").equals(method)) {
				return genericBucket.toString();
			} else if (Object.class.getDeclaredMethod("equals", Object.class).equals(method)) {
				return proxy.getClass().equals(args[0].getClass()) &&
						genericBucket.equals(((Proxy)args[0]).getProxyState());
			}
			throw new UnsupportedOperationException("Method " + method.toGenericString() + " is not supported right now!");
		}
	}
}
