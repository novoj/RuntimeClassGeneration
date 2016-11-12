package cz.novoj.generation.contract.model;

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
				GenericBucketProxyGenerator.class.getClassLoader(), new Class[] {contract},
				new GenericBucketInvocationHandler()
		);
	}

	class GenericBucketInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			throw new UnsupportedOperationException("Method " + method.toGenericString() + " is not supported right now!");
		}

	}
}
