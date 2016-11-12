package cz.novoj.generation.proxyGenerator.implementation.bytebuddy;

import cz.novoj.generation.proxyGenerator.infrastructure.ProxyStateAccessor;
import lombok.extern.apachecommons.CommonsLog;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@CommonsLog
public class ByteBuddyProxyGenerator {
    private static final String INVOCATION_HANDLER_FIELD = "dispatcherInvocationHandler";
    private static final Map<List<Class<?>>, Class<?>> CACHED_PROXY_CLASSES = new ConcurrentHashMap<>(64);
    private static final Map<Class<?>, Constructor<?>> CACHED_PROXY_CONSTRUCTORS = new ConcurrentHashMap<>(64);
    private static final Method OBJECT_HASH_CODE;
    private static final Method OBJECT_EQUALS;
    private static final Method OBJECT_TO_STRING;
    private static final AtomicInteger CLASS_COUNTER = new AtomicInteger(0);

    static {
        try {
            OBJECT_TO_STRING = Object.class.getMethod("toString");
            OBJECT_HASH_CODE = Object.class.getMethod("hashCode");
            OBJECT_EQUALS = Object.class.getMethod("equals", Object.class);
        } catch (NoSuchMethodException e) {
            //not expected
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
	public static <T> T instantiate(InvocationHandler invocationHandler, Class<?>... interfaces) {
        return instantiateProxy(
				(Class<T>)getProxyClass(interfaces),
                invocationHandler
        );
    }

    private static Class<?> getProxyClass(Class<?>... interfaces) {
		// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
        return CACHED_PROXY_CLASSES.computeIfAbsent(
				// CACHE KEY
                Arrays.asList(interfaces),
				// LAMBDA THAT CREATES OUR PROXY CLASS
                classes -> {

                    DynamicType.Builder<?> builder;

                    final Class<?> superClass;
					// IF WE PROXY ABSTRACT CLASS, WE HAVE A RULE THAT IT HAS TO BE FIRST IN LIST
                    if (interfaces[0].isInterface()) {
						// FIRST IS INTERFACE
						// AUTOMATICALLY ADD PROXYSTATEACCESSOR CLASS TO EVERY OUR PROXY WE CREATE
                        final Class<?>[] finalContract = new Class[interfaces.length + 1];
                        finalContract[0] = ProxyStateAccessor.class;
                        System.arraycopy(interfaces, 0, finalContract, 1, interfaces.length);
						// WE'LL EXTEND OBJECT CLASS AND IMPLEMENT ALL INTERFACES
                        superClass = Object.class;
                        builder = new ByteBuddy().subclass(Object.class).implement(finalContract);
                    } else {
						// FIRST IS ABSTRACT CLASS
                        superClass = interfaces[0];
						// AUTOMATICALLY ADD PROXYSTATEACCESSOR CLASS TO EVERY OUR PROXY WE CREATE
                        interfaces[0] = ProxyStateAccessor.class;
						// WE'LL EXTEND ABSTRACT CLASS AND IMPLEMENT ALL OTHER INTERFACES
                        builder = new ByteBuddy().subclass(superClass).implement(interfaces);
                    }

                    final Class<?> proxyClass = builder
							// WE CAN DEFINE OUR OWN PACKAGE AND NAME FOR THE CLASS
                            .name("cz.novoj.generation.model.proxy." + interfaces[0].getSimpleName() + '_' + CLASS_COUNTER.incrementAndGet())
							// WE'LL CREATE PRIVATE FINAL FIELD FOR STORING OUR INVOCATION HANDLER ON INSTANCE
                            .defineField(INVOCATION_HANDLER_FIELD, ByteBuddyDispatcherInvocationHandler.class, Modifier.PRIVATE + Modifier.FINAL)
							// LET'S HAVE PUBLIC CONSTRUCTOR
                            .defineConstructor(Modifier.PUBLIC)
							// ACCEPTING SINGLE ARGUMENT OF OUR INVOCATION HANDLER
                            .withParameter(ByteBuddyDispatcherInvocationHandler.class)
							// AND THIS CONSTRUCTOR WILL
                            .intercept(
                                    MethodCall
											// CALL DEFAULT (NON-ARG) CONSTRUCTOR ON SUPERCLASS
											.invoke(getDefaultConstructor(superClass))
                                            .onSuper()
											// AND THEN FILL PRIVATE FIELD WITH PASSED INVOCATION HANDLER
                                            .andThen(
                                                FieldAccessor.ofField(INVOCATION_HANDLER_FIELD).setsArgumentAt(0)
                                            )
                            )
							// AND INTERCEPTS ALL ABSTRACT AND OBJECT DEFAULT METHODS
                            .method(
                                    ElementMatchers.isAbstract()
                                            .or(ElementMatchers.is(OBJECT_TO_STRING))
                                            .or(ElementMatchers.is(OBJECT_HASH_CODE))
                                            .or(ElementMatchers.is(OBJECT_EQUALS))
                            )
							// AND DELEGATE CALL TO OUR INVOCATION HANDLER STORED IN PRIVATE FIELD OF THE CLASS
                            .intercept(InvocationHandlerAdapter.toField(INVOCATION_HANDLER_FIELD))
							// NOW CREATE THE BYTE-CODE
                            .make()
							// AND LOAD IT IN CURRENT CLASSLOADER
                            .load(ByteBuddyProxyGenerator.class.getClassLoader())
							// RETURN
                            .getLoaded();


                    log.info("Created proxy class: " + proxyClass.getName());
                    return proxyClass;
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class<T> proxyClass, InvocationHandler invocationHandler) {
        try {
			// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
            Constructor<T> constructor = (Constructor<T>)CACHED_PROXY_CONSTRUCTORS.computeIfAbsent(
            		// CACHE KEY
                    proxyClass,
					// LAMBDA THAT FINDS OUT MISSING CONSTRUCTOR
					aClass -> {
                        try {
                            return proxyClass.getConstructor(ByteBuddyDispatcherInvocationHandler.class);
                        } catch (NoSuchMethodException e) {
                            throw new IllegalArgumentException("What the heck? Can't find proper constructor on proxy: " + e.getMessage(), e);
                        }
                    }
            );
            return constructor.newInstance(invocationHandler);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
	private static <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
		// COMPUTE IF ABSENT = GET FROM MAP, IF MISSING -> COMPUTE, STORE AND RETURN RESULT OF LAMBDA
        return (Constructor<T>)CACHED_PROXY_CONSTRUCTORS.computeIfAbsent(
        		// CACHE KEY
                clazz,
				// LAMBDA THAT FINDS OUT MISSING CONSTRUCTOR
				aClass -> {
                    try {
                        return clazz.getConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new IllegalArgumentException("What the heck? Can't find default constructor on abstract class: " + e.getMessage(), e);
                    }
                }
        );
    }

}
