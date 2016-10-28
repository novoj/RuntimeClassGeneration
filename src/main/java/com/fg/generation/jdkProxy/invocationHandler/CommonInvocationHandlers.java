package com.fg.generation.jdkProxy.invocationHandler;

import com.fg.generation.jdkProxy.invocationHandler.infrastructure.ContextWiseMethodInvocationHandler;
import com.fg.generation.jdkProxy.invocationHandler.infrastructure.MethodClassification;
import com.fg.generation.jdkProxy.invocationHandler.infrastructure.Proxy;

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

    static MethodClassification<Void, Object> toStringMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Object.class.getDeclaredMethod("toString")),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.toString()
        );
    }

    static MethodClassification<Void, Object> hashCodeMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Object.class.getDeclaredMethod("hashCode")),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.hashCode()
        );
    }

    static MethodClassification<Void, Object> equalsMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Object.class.getDeclaredMethod("equals", Object.class)),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) ->
                                        proxy.getClass().equals(args[0].getClass()) &&
                                        proxyState.equals(((Proxy)args[0]).getProxyState())
        );
    }

    static MethodClassification<Void, Object> finalizeInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Proxy.class.getDeclaredMethod("finalize")),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> null);
    }

    static ContextWiseMethodInvocationHandler<Void> missingImplementationInvokerWithContext() {
        return (proxy, method, args, proxyState) -> missingImplementationInvoker();
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

}
