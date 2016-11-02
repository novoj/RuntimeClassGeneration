package cz.novoj.generation.contract;

import cz.novoj.generation.proxyGenerator.infrastructure.ContextWiseMethodInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import cz.novoj.generation.proxyGenerator.infrastructure.Proxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface StandardJavaMethods {
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

    static MethodClassification realMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> !Modifier.isAbstract(method.getModifiers()),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> method.invoke(proxy, args)
        );
    }

    static MethodClassification<Void, Object, Proxy> toStringMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Object.class.getDeclaredMethod("toString")),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.toString()
        );
    }

    static MethodClassification<Void, Object, Proxy> hashCodeMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Object.class.getDeclaredMethod("hashCode")),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.hashCode()
        );
    }

    static MethodClassification<Void, Object, Proxy> equalsMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.equals(Object.class.getDeclaredMethod("equals", Object.class)),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) ->
                                        proxy.getClass().equals(args[0].getClass()) &&
                                        proxyState.equals(((Proxy)args[0]).getProxyState())
        );
    }

    static ContextWiseMethodInvocationHandler missingImplementationInvoker() {
        return (proxy, method, args, proxyState) -> {
            throw new UnsupportedOperationException(
                    "Method " + method.toGenericString() + " is not supported by this proxy!"
            );
        };
    }

}
