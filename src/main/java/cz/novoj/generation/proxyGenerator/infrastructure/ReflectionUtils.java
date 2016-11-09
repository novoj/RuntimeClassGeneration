package cz.novoj.generation.proxyGenerator.infrastructure;

import java.lang.reflect.Method;

/**
 * Created by Rodina Novotnych on 05.11.2016.
 */
public interface ReflectionUtils {

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
