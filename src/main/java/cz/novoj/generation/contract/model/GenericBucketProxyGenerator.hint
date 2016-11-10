package cz.novoj.generation.contract.model;

import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.JdkProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;
import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface GenericBucketProxyGenerator {

	String GET = "get";
	String SET = "set";
	String GET_PROPERTY = "getProperty";
	String SET_PROPERTY = "setProperty";
	String GET_PROPERTIES = "getProperties";

	static MethodClassification<String, GenericBucket, PropertyAccessor> getterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET) && method.getParameterCount() == 0,
        /* methodContext */ method -> uncapitalize(method.getName().substring(GET.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.get(methodContext)
		);
	}

	static MethodClassification<String, GenericBucket, PropertyAccessor> setterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(SET) && method.getParameterCount() == 1,
        /* methodContext */ method -> uncapitalize(method.getName().substring(SET.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.put(methodContext, args[0])
		);
	}

	static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertiesInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, GET_PROPERTIES),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState
		);
	}

	static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, GET_PROPERTY, String.class),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.get(args[0])
		);
	}

	static MethodClassification<Void, GenericBucket, PropertyAccessor> setPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, SET_PROPERTY, String.class, Object.class),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.put(String.valueOf(args[0]), args[1])
		);
	}

	static <T> T instantiate(Class<T> contract) {
		return JdkProxyGenerator.instantiate(
				new JdkProxyDispatcherInvocationHandler<>(
						new GenericBucket(64),
						getPropertiesInvoker(),
						getPropertyInvoker(),
						setPropertyInvoker(),
						getterInvoker(),
						setterInvoker()
				),
				contract
		);
	}

}