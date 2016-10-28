package com.fg.generation.jdkProxy.invocationHandler;

import com.fg.generation.jdkProxy.invocationHandler.infrastructure.ContextWiseMethodInvocationHandler;
import com.fg.generation.jdkProxy.invocationHandler.infrastructure.MethodClassification;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface CommonInvocationHandlers {
    Function<Method, Void> NO_CONTEXT = method -> null;

    static MethodClassification defaultMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       Method::isDefault,
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
                                Constructor<MethodHandles.Lookup> constructor =
                                        MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);

                                constructor.setAccessible(true);

                                Class<?> declaringClass = method.getDeclaringClass();
                                return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                                        .unreflectSpecial(method, declaringClass)
                                        .bindTo(proxy)
                                        .invokeWithArguments(args);
                            }
        );
    }

    static ContextWiseMethodInvocationHandler<Void> defaultMethodInvokerWithContext() {
        return (proxy, method, args, proxyState) -> defaultMethodInvoker();
    }

    static MethodClassification missingImplementationInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> true,
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
                                throw new UnsupportedOperationException(
                                        "Method " + method.toGenericString() + " is not supported by this proxy!"
                                );
                            }
        );
    }

    static ContextWiseMethodInvocationHandler<Void> missingImplementationInvokerWithContext() {
        return (proxy, method, args, proxyState) -> missingImplementationInvoker();
    }

}
