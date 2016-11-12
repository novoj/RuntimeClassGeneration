package cz.novoj.generation.contract;

import cz.novoj.generation.model.ProxyStateAccessor;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.NO_CONTEXT;
import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;


public interface StandardJavaMethods {

	/** METHOD CONTRACT: String toString() **/
    static MethodClassification<ProxyStateAccessor, Void, Object> toStringMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "toString"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxyStateAccessor, method, args, methodContext, proxyState) -> proxyState.toString()
        );
    }

	/** METHOD CONTRACT: int hashCode() **/
    static MethodClassification<ProxyStateAccessor, Void, Object> hashCodeMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "hashCode"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxyStateAccessor, method, args, methodContext, proxyState) -> proxyState.hashCode()
        );
    }

	/** METHOD CONTRACT: boolean equals(Object o) **/
    static MethodClassification<ProxyStateAccessor, Void, Object> equalsMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "equals", Object.class),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxyStateAccessor, method, args, methodContext, proxyState) ->
                                        proxyStateAccessor.getClass().equals(args[0].getClass()) &&
                                        proxyState.equals(((ProxyStateAccessor)args[0]).getProxyState())
        );
    }

	/** METHOD CONTRACT: catch everything else and throw exception **/
    static CurriedMethodContextInvocationHandler<?, ?> missingImplementationInvoker() {
        return (proxy, method, args, proxyState) -> {
            throw new UnsupportedOperationException(
                    "Method " + method.toGenericString() + " is not supported by this proxy!"
            );
        };
    }

}
