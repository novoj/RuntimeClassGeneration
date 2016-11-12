package cz.novoj.generation.proxyGenerator.infrastructure;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ReflectionUtils {

	/**
	 * Retrieves parameter names from the method via. reflection.
	 * Requires "-parameters" argument to be used during compilation.
	 *
	 * @param method
	 * @return
	 */
	static String[] getParameterNames(Method method) {
		final String[] parameterNames = new String[method.getParameterCount()];
		final Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			final Parameter param = parameters[i];
			if (!param.isNamePresent()) {
				throw new IllegalStateException("Source code is not compiled with -parameters argument!");
			}
			parameterNames[i] = param.getName();
		}
		return parameterNames;
	}

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

	/**
	 * Finds method handle for the default method.
	 * @param method
	 * @return
	 */
	static MethodHandle findMethodHandle(Method method) {
		try {

			final Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class, int.class);
			constructor.setAccessible(true);

			final Class<?> declaringClass = method.getDeclaringClass();
			return constructor
					.newInstance(declaringClass, Lookup.PRIVATE)
					.unreflectSpecial(method, declaringClass);

		} catch (Exception ex) {
			throw new IllegalArgumentException("Can't find handle to method " + method.toGenericString() + "!", ex);
		}

	}

}
