package cz.novoj.generation.proxyGenerator.implementation.bytebuddy;

import lombok.extern.apachecommons.CommonsLog;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@CommonsLog
public final class ByteBuddyProxyGenerator {
    private static Map<List<Class>, Class> cachedProxyClasses = new ConcurrentHashMap<>(64);
    private static Map<Class, Constructor> cachedProxyConstructors = new ConcurrentHashMap<>(64);

    public static <T> T instantiate(InvocationHandler invocationHandler, Class... interfaces) {
        return instantiateProxy(
                getProxyClass(interfaces),
                invocationHandler
        );
    }

    private static Class getProxyClass(Class... interfaces) {
        return cachedProxyClasses.computeIfAbsent(
                Arrays.asList(interfaces),
                classes -> {

                    DynamicType.Builder<?> builder;

                    Class superClass;
                    if (interfaces[0].isInterface()) {
                        final Class[] finalContract = new Class[interfaces.length + 1];
                        finalContract[0] = cz.novoj.generation.proxyGenerator.infrastructure.Proxy.class;
                        System.arraycopy(interfaces, 0, finalContract, 1, interfaces.length);

                        superClass = Object.class;
                        builder = new ByteBuddy().subclass(Object.class).implement(finalContract);
                    } else {
                        superClass = interfaces[0];
                        interfaces[0] = cz.novoj.generation.proxyGenerator.infrastructure.Proxy.class;
                        builder = new ByteBuddy().subclass(superClass).implement(interfaces);
                    }

                    Class proxyClass = builder
                            .defineField("dispatcherInvocationHandler", ByteBuddyDispatcherInvocationHandler.class, Modifier.PRIVATE + Modifier.FINAL)
                            .defineConstructor(Modifier.PUBLIC)
                            .withParameter(ByteBuddyDispatcherInvocationHandler.class)
                            .intercept(
                                    MethodCall.invoke(getDefaultConstructor(superClass))
                                            .onSuper()
                                            .andThen(
                                                    FieldAccessor.ofField("dispatcherInvocationHandler")
                                                            .setsArgumentAt(0)
                                            )
                            )
                            .method(ElementMatchers.any())
                            .intercept(InvocationHandlerAdapter.toField("dispatcherInvocationHandler"))
                            .make()
                            .load(ByteBuddyProxyGenerator.class.getClassLoader())
                            .getLoaded();


                    log.info("Created proxy class: " + proxyClass.getName());
                    return proxyClass;
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class proxyClass, InvocationHandler invocationHandler) {
        try {
            Constructor constructor = cachedProxyConstructors.computeIfAbsent(
                    proxyClass, aClass -> {
                        try {
                            return proxyClass.getConstructor(ByteBuddyDispatcherInvocationHandler.class);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException("What the heck? Can't find proper constructor on proxy: " + e.getMessage(), e);
                        }
                    }
            );
            return (T) constructor.newInstance(invocationHandler);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
    }

    private static Constructor getDefaultConstructor(Class clazz) {
        return cachedProxyConstructors.computeIfAbsent(
                clazz, aClass -> {
                    try {
                        return clazz.getConstructor();
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("What the heck? Can't find default constructor on abstract class: " + e.getMessage(), e);
                    }
                }
        );
    }

}
