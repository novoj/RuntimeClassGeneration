package cz.novoj.generation.proxyGenerator.infrastructure;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * Encapsulates method call context and abstracts from specific of super method call invocation of different
 * byte generation libraries.
 */
@Data
public abstract class MethodCall {
	protected final Object proxy;
	protected final Method method;
	protected final Object[] args;

	public abstract Object invokeSuper();

	@Override
	public String toString() {
		return method.toGenericString();
	}

}
