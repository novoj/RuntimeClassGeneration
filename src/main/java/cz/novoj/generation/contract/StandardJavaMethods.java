package cz.novoj.generation.contract;

import cz.novoj.generation.contract.model.GenericBucket;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;
import cz.novoj.generation.proxyGenerator.infrastructure.ProxyStateAccessor;
import cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.NO_CONTEXT;
import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface StandardJavaMethods {

	/** METHOD CONTRACT: catch all default methods and delegate calls to them **/
    static MethodClassification<ProxyStateAccessor, MethodHandle, GenericBucket> defaultMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       Method::isDefault,
        /* methodContext */ ReflectionUtils::findMethodHandle,
        /* invocation */    (proxy, method, args, methodContext, proxyState) ->
								methodContext.bindTo(proxy).invokeWithArguments(args)
        );
    }

	/** METHOD CONTRACT: catch all real (not abstract) methods and delegate calls to them **/
    static MethodClassification<ProxyStateAccessor, Void, GenericBucket> realMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> !Modifier.isAbstract(method.getModifiers()),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> method.invoke(proxy, args)
        );
    }

	/** METHOD CONTRACT: String toString() **/
    static MethodClassification<ProxyStateAccessor, Void, Object> toStringMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "toString"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.toString()
        );
    }

	/** METHOD CONTRACT: int hashCode() **/
    static MethodClassification<ProxyStateAccessor, Void, Object> hashCodeMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "hashCode"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.hashCode()
        );
    }

	/** METHOD CONTRACT: boolean equals(Object o) **/
    static MethodClassification<ProxyStateAccessor, Void, Object> equalsMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "equals", Object.class),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxy, method, args, methodContext, proxyState) ->
                                        proxy.getClass().equals(args[0].getClass()) &&
                                        proxyState.equals(((ProxyStateAccessor)args[0]).getProxyState())
        );
    }

	/** METHOD CONTRACT: catch everything else and throw exception **/
	static CurriedMethodContextInvocationHandler<?,?> missingImplementationInvoker() {
        return (proxy, method, args, proxyState) -> {
            throw new UnsupportedOperationException(
                    "Method " + method.toGenericString() + " is not supported by this proxy!"
            );
        };
    }

}
