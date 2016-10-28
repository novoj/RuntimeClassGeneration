package com.fg.generation.jdkProxy.invocationHandler.infrastructure;

import java.lang.reflect.Method;

/**
 * Created by Rodina Novotnych on 28.10.2016.
 */
@FunctionalInterface
public interface MethodMatcher {

    boolean matches(Method method);

}
