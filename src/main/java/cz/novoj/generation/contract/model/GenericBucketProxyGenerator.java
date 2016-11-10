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

	/** HANDLES: String getFirstName(); **/
	static MethodClassification<String, GenericBucket, PropertyAccessor> getterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	/** HANDLES: void setFirstName(String firstName); **/
	static MethodClassification<String, GenericBucket, PropertyAccessor> setterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	/** HANDLES: Map<String, Object> getProperties(); **/
	static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertiesInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	/** HANDLES: Object getProperty(String name); **/
	static MethodClassification<Void, GenericBucket, PropertyAccessor> getPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	/** HANDLES: void setProperty(String name, Object value); **/
	static MethodClassification<Void, GenericBucket, PropertyAccessor> setPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
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
