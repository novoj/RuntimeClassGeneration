package com.fg.generation.specific.bytebuddy;

import lombok.extern.apachecommons.CommonsLog;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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
public final class BytebuddyProxyGenerator {
    private static Map<List<Class>, Class> cachedProxyClasses = new ConcurrentHashMap<>(64);

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

                    if (interfaces[0].isInterface()) {
                        builder = new ByteBuddy().subclass(Object.class).implement((Type[]) interfaces);
                    } else {
                        Type[] interfaceType = new Type[interfaces.length - 1];
                        for (int i = 1; i < interfaces.length; i++) {
                            interfaceType[i - 1] = interfaces[i];
                        } // todo copyrangeof?
                        builder = new ByteBuddy().subclass(interfaces[0]).implement(interfaceType);
                    }

                    Class proxyClass = builder
                            .defineField("dispatcherInvocationHandler", ByteBuddyDispatcherInvocationHandler.class, Modifier.PRIVATE + Modifier.FINAL)
                            .defineConstructor(Modifier.PUBLIC)
                            .withParameter(ByteBuddyDispatcherInvocationHandler.class)
                            .intercept(MethodCall.invokeSuper().andThen(FieldAccessor.ofField("dispatcherInvocationHandler").setsArgumentAt(0)))
                            .method(ElementMatchers.any())
                            .intercept(InvocationHandlerAdapter.toField("dispatcherInvocationHandler"))
                            .make()
                            .load(BytebuddyProxyGenerator.class.getClassLoader())
                            .getLoaded();


                    log.info("Created proxy class: " + proxyClass.getName());
                    return proxyClass;
                });
    }

    @SuppressWarnings("unchecked")
    private static <T> T instantiateProxy(Class proxyClass, InvocationHandler invocationHandler) {
        try {
            T proxy = (T) proxyClass.newInstance();

            return proxy;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("What the heck? Can't create proxy: " + e.getMessage(), e);
        }
    }

}
