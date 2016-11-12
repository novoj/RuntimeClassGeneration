package cz.novoj.generation.contract.model;

import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.JavassistDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.JavassistProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.MethodClassification;

import static cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils.isMethodDeclaredOn;
import static org.apache.commons.lang.StringUtils.uncapitalize;


public interface GenericBucketProxyGenerator {

	String GET = "get";
	String SET = "set";
	String GET_PROPERTY = "getProperty";
	String SET_PROPERTY = "setProperty";
	String GET_PROPERTIES = "getProperties";

	/** METHOD CONTRACT: Object getSomething() **/
	static MethodClassification<PropertyAccessor, String, GenericBucket> getterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(GET) && method.getParameterCount() == 0,
        /* methodContext */ method -> uncapitalize(method.getName().substring(GET.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.get(methodContext)
		);
	}

	/** METHOD CONTRACT: void setSomething(Object value) **/
	static MethodClassification<PropertyAccessor, String, GenericBucket> setterInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(SET) && method.getParameterCount() == 1,
        /* methodContext */ method -> uncapitalize(method.getName().substring(SET.length())),
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
			proxyState.set(methodContext, args[0]);
			return null;
		});
	}

	/** METHOD CONTRACT: Map<String,Object> getProperties() **/
	static MethodClassification<PropertyAccessor, Void, GenericBucket> getPropertiesInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, GET_PROPERTIES),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> proxyState.getData()
		);
	}

	/** METHOD CONTRACT: Object getProperty(String propertyName) **/
	static MethodClassification<PropertyAccessor, Void, GenericBucket> getPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, GET_PROPERTY, String.class),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
			proxyState.get(String.valueOf(args[0]));
			return null;
		});
	}

	/** METHOD CONTRACT: void setProperty(String propertyName, Object propertyValue) **/
	static MethodClassification<PropertyAccessor, Void, GenericBucket> setPropertyInvoker() {
		return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, SET_PROPERTY, String.class, Object.class),
        /* methodContext */ method -> null,
        /* invocation */    (proxy, method, args, methodContext, proxyState) -> {
			proxyState.set(String.valueOf(args[0]), args[1]);
			return null;
		});
	}

	static <T> T instantiate(Class<T> contract) {
		return JavassistProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
				new JavassistDispatcherInvocationHandler<>(
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
				contract);
	}

}