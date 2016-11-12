package cz.novoj.generation.contract.model;

import cz.novoj.generation.proxyGenerator.JdkProxyGenerator;
import cz.novoj.generation.model.ProxyStateAccessor;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface GenericBucketProxyGenerator {

	static <T> T instantiate(Class<T> contract) {
		return JdkProxyGenerator.instantiate(new GenericBucketInvocationHandler(), contract);
	}

	class GenericBucketInvocationHandler implements InvocationHandler {
		private final GenericBucket genericBucket = new GenericBucket();

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (ProxyStateAccessor.class.getDeclaredMethod("getProxyState").equals(method)) {
				return genericBucket;
			} else if ("getProperties".equals(method.getName())) {
				return genericBucket.getData();
			} else if (method.getName().startsWith("get")) {
				final String propertyName = StringUtils.uncapitalize(method.getName().substring(3));
				return genericBucket.get(propertyName);
			} else if (method.getName().startsWith("set")) {
				final String propertyName = StringUtils.uncapitalize(method.getName().substring(3));
				genericBucket.set(propertyName, args[0]);
				return null;
			} else if (Object.class.getDeclaredMethod("hashCode").equals(method)) {
				return genericBucket.hashCode();
			} else if (Object.class.getDeclaredMethod("toString").equals(method)) {
				return genericBucket.toString();
			} else if (Object.class.getDeclaredMethod("equals", Object.class).equals(method)) {
				return proxy.getClass().equals(args[0].getClass()) &&
						genericBucket.equals(((ProxyStateAccessor)args[0]).getProxyState());
			}
			throw new UnsupportedOperationException("Method " + method.toGenericString() + " is not supported right now!");
		}
	}
}
