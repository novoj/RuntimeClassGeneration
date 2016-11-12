package cz.novoj.generation.proxyGenerator.infrastructure;

import java.lang.reflect.Method;

public interface ReflectionUtils {

	/**
	 * Returns true if method equals method onClass with the same name and same parameters.
	 *
	 * @param method
	 * @param onClass
	 * @param withSameName
	 * @param withSameTypes
	 * @return
	 */
    static boolean isMethodDeclaredOn(Method method, Class<?> onClass, String withSameName, Class<?>... withSameTypes) {
        try {
            return method.equals(onClass.getMethod(withSameName, withSameTypes));
        } catch (Exception ex) {
            throw new IllegalStateException(
                "Matcher " + onClass.getName() + " failed to process " + method.toGenericString() + ": " + ex.getMessage(), ex
            );
        }
    }
}
