package cz.novoj.generation.contract.dao.helper;

import com.sun.beans.WeakCache;
import cz.novoj.generation.contract.model.PropertyAccessor;

import java.beans.FeatureDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface ReflectionUtils {
    WeakCache<Class<?>, Map<String, PropertyDescriptor>> propertyAccessorCache = new WeakCache<>();
    Object monitor = new Object();

    static <U extends PropertyAccessor> String[] getParameterNames(Method method) {
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

    static <U extends PropertyAccessor> Object getProperty(Object object, String propertyName) {
        try {
            Map<String, PropertyDescriptor> beanDescriptors = propertyAccessorCache.get(object.getClass());
            if (beanDescriptors == null) {
                beanDescriptors = Stream.of(Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors())
                                .collect(toMap(FeatureDescriptor::getName, identity()));

                synchronized (monitor) {
                    propertyAccessorCache.put(object.getClass(), beanDescriptors);
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

    static boolean isMethodDeclaredOn(Method method, Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return method.equals(clazz.getMethod(methodName, parameterTypes));
        } catch (Exception ex) {
            throw new IllegalStateException(
                "Matcher " + clazz.getName() + " failed to process " + method.toGenericString() + ": " + ex.getMessage(), ex
            );
        }
    }
}
