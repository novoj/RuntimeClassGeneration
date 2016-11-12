package cz.novoj.generation.contract;

import cz.novoj.generation.model.ProxyStateAccessor;
import cz.novoj.generation.proxyGenerator.infrastructure.CurriedMethodContextInvocationHandler;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification.NO_CONTEXT;
import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;


public interface StandardJavaMethods {

    static MethodClassification<Void, Object, ProxyStateAccessor> toStringMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "toString"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxyStateAccessor, method, args, methodContext, proxyState) -> proxyState.toString()
        );
    }

    static MethodClassification<Void, Object, ProxyStateAccessor> hashCodeMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "hashCode"),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxyStateAccessor, method, args, methodContext, proxyState) -> proxyState.hashCode()
        );
    }

    static MethodClassification<Void, Object, ProxyStateAccessor> equalsMethodInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, Object.class, "equals", Object.class),
        /* methodContext */ NO_CONTEXT,
        /* invocation */    (proxyStateAccessor, method, args, methodContext, proxyState) ->
                                        proxyStateAccessor.getClass().equals(args[0].getClass()) &&
                                        proxyState.equals(((ProxyStateAccessor)args[0]).getProxyState())
        );
    }

    static CurriedMethodContextInvocationHandler<?, ?> missingImplementationInvoker() {
        return (proxy, method, args, proxyState) -> {
            throw new UnsupportedOperationException(
                    "Method " + method.toGenericString() + " is not supported by this proxy!"
            );
        };
    }

}
