package cz.novoj.generation.contract.model;

import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDeserializationProxyFactory;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibDeserializationProxyFactory;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistDeserializationProxyFactory;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.javassist.JavassistProxyGenerator;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyDeserializationProxyFactory;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyDispatcherInvocationHandler;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkProxyGenerator;
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
        /* invocation */    (methodCall, proxy, args, methodContext, proxyState) -> proxyState.get(methodContext)
        );
    }

	/** METHOD CONTRACT: void setSomething(Object value) **/
    static MethodClassification<PropertyAccessor, String, GenericBucket> setterInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> method.getName().startsWith(SET) && method.getParameterCount() == 1,
        /* methodContext */ method -> uncapitalize(method.getName().substring(SET.length())),
        /* invocation */    (methodCall, proxy, args, methodContext, proxyState) -> {
        	proxyState.set(methodContext, args[0]);
			return null;
		});
    }

	/** METHOD CONTRACT: Map<String,Object> getProperties() **/
    static MethodClassification<PropertyAccessor, Void, GenericBucket> getPropertiesInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, GET_PROPERTIES),
        /* methodContext */ method -> null,
        /* invocation */    (methodCall, proxy, args, methodContext, proxyState) -> proxyState.getData()
        );
    }

	/** METHOD CONTRACT: Object getProperty(String propertyName) **/
    static MethodClassification<PropertyAccessor, Void, GenericBucket> getPropertyInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, GET_PROPERTY, String.class),
        /* methodContext */ method -> null,
        /* invocation */    (methodCall, proxy, args, methodContext, proxyState) -> {
        	proxyState.get(String.valueOf(args[0]));
        	return null;
		});
    }

	/** METHOD CONTRACT: void setProperty(String propertyName, Object propertyValue) **/
    static MethodClassification<PropertyAccessor, Void, GenericBucket> setPropertyInvoker() {
        return new MethodClassification<>(
        /* matcher */       method -> isMethodDeclaredOn(method, PropertyAccessor.class, SET_PROPERTY, String.class, Object.class),
        /* methodContext */ method -> null,
        /* invocation */    (methodCall, proxy, args, methodContext, proxyState) -> {
        	proxyState.set(String.valueOf(args[0]), args[1]);
		    return null;
		});
    }

    static <T> T instantiateJdkProxy(Class<T> contract) {
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
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JdkProxyDeserializationProxyFactory.INSTANCE)
                ),
				// interfaces to implement
                contract, SerializableProxy.class
        );
    }

	static <T> T instantiateCglibProxy(Class<T> contract) {
		return CglibProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
				new CglibDispatcherInvocationHandler<>(
						// proxy state
						new GenericBucket(),
						// list of features - order is important
						getPropertiesInvoker(),
						getPropertyInvoker(),
						setPropertyInvoker(),
						getterInvoker(),
						setterInvoker(),
						SerializableProxy.getWriteReplaceMethodInvoker(CglibDeserializationProxyFactory.INSTANCE)
				),
				// interfaces to implement
				contract, SerializableProxy.class
		);
	}

    static <T> T instantiateJavassistProxy(Class<T> contract) {
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
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JavassistDeserializationProxyFactory.INSTANCE)
                ),
				// interfaces to implement
                contract, SerializableProxy.class);
    }

    static <T> T instantiateByteBuddyProxy(Class<T> contract) {
        return ByteBuddyProxyGenerator.instantiate(
				// create invocation handler delegating calls to "classifications" - ie atomic features of the proxy
                new ByteBuddyDispatcherInvocationHandler<>(
						// proxy state
                        new GenericBucket(),
						// list of features - order is important
                        getPropertiesInvoker(),
                        getPropertyInvoker(),
                        setPropertyInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(ByteBuddyDeserializationProxyFactory.INSTANCE)
                ),
				// interfaces to implement
                contract, SerializableProxy.class);
    }

}
