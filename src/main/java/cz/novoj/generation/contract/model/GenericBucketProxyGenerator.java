package cz.novoj.generation.contract.model;

import cz.novoj.generation.model.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.JdkProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;
import static org.apache.commons.lang.StringUtils.uncapitalize;


public interface GenericBucketProxyGenerator {

	String GET = "get";
	String SET = "set";
	String GET_PROPERTY = "getProperty";
	String SET_PROPERTY = "setProperty";
	String GET_PROPERTIES = "getProperties";

	static MethodClassification<String, GenericBucket, PropertyAccessor> getterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	static MethodClassification<String, GenericBucket, PropertyAccessor> setterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
        	/* TODO */
		});
	}

	static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertiesInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	static MethodClassification<Void, GenericBucket, PropertyAccessor> setPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
        	/* TODO */
        });
	}

	static <T> T instantiate(Class<T> contract) {
		return JdkProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
				new JdkProxyDispatcherInvocationHandler<>(
						// proxy state
						new GenericBucket(),
						// list of features - order is important
						getPropertiesInvoker(),
						getPropertyInvoker(),
						setPropertyInvoker(),
						getterInvoker(),
						setterInvoker()
				),
				// interfaces to implement
				contract
		);
	}

}