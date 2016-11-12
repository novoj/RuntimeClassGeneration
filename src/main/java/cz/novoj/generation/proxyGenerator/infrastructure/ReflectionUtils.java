package cz.novoj.generation.proxyGenerator.infrastructure;

import com.sun.beans.WeakCache;

import java.beans.FeatureDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public interface ReflectionUtils {
    WeakCache<Class<?>, Map<String, PropertyDescriptor>> PROPERTY_ACCESSOR_CACHE = new WeakCache<>();
    Object MONITOR = new Object();

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

    static Object getProperty(Object object, String propertyName) {
        try {
            Map<String, PropertyDescriptor> beanDescriptors = PROPERTY_ACCESSOR_CACHE.get(object.getClass());
            if (beanDescriptors == null) {
                beanDescriptors = Stream.of(Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors())
                                .collect(toMap(FeatureDescriptor::getName, identity()));

                synchronized (MONITOR) {
                    PROPERTY_ACCESSOR_CACHE.put(object.getClass(), beanDescriptors);
                }

            }
            return ofNullable(beanDescriptors.get(propertyName))
                    .map(pd -> {
                        try {
                            return pd.getReadMethod().invoke(object);
                        } catch (Exception e) {
                            // error in getter?!
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse(null);

        } catch (Exception ex) {
            throw new RuntimeException("Class introspection unexpectedly failed.", ex);
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
