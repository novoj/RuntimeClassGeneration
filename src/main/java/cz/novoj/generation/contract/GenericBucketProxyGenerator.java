package cz.novoj.generation.contract;

import cz.novoj.generation.contract.model.GenericBucket;
import cz.novoj.generation.model.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.JdkProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;


public interface GenericBucketProxyGenerator {

	/** METHOD CONTRACT: Object getSomething() **/
	static MethodClassification<PropertyAccessor, String, GenericBucket> getterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	/** METHOD CONTRACT: void setSomething(Object value) **/
	static MethodClassification<PropertyAccessor, String, GenericBucket> setterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
        	/* TODO */
		});
	}

	/** METHOD CONTRACT: Map<String,Object> getProperties() **/
	static MethodClassification<PropertyAccessor, Void, GenericBucket> getPropertiesInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	/** METHOD CONTRACT: Object getProperty(String propertyName) **/
	static MethodClassification<PropertyAccessor, Void, GenericBucket> getPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> /* TODO */,
        /* methodContext */ method -> /* TODO */,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> /* TODO */
		);
	}

	/** METHOD CONTRACT: void setProperty(String propertyName, Object propertyValue) **/
	static MethodClassification<PropertyAccessor, Void, GenericBucket> setPropertyInvoker() {
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