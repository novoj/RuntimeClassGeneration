package com.fg.generation.specific.javassist;

import com.fg.generation.infrastructure.SerializableProxy;

import java.util.Map;

import static com.fg.generation.contract.GenericBucketProxyGenerator.*;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public class JavassistDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<Map<String, Object>> {
    public static JavassistDeserializationProxyFactory INSTANCE = new JavassistDeserializationProxyFactory();

    private JavassistDeserializationProxyFactory() {
        //singleton
    }

    @Override
    public Object deserialize(Map<String, Object> target, Class[] interfaces) {
        return JavassistProxyGenerator.instantiate(
                new JavassistDispatcherInvocationHandler<>(
                        target,
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JavassistDeserializationProxyFactory.INSTANCE)
                ),
                interfaces);
    }
}
