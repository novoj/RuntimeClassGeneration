package com.fg.generation.specific.bytebuddy;

import com.fg.generation.infrastructure.SerializableProxy;

import java.util.Map;

import static com.fg.generation.contract.GenericBucketProxyGenerator.*;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public class ByteBuddyDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<Map<String, Object>> {
    public static ByteBuddyDeserializationProxyFactory INSTANCE = new ByteBuddyDeserializationProxyFactory();

    private ByteBuddyDeserializationProxyFactory() {
        // singleton
    }

    @Override
    public Object deserialize(Map<String, Object> target, Class[] interfaces) {
        return ByteBuddyProxyGenerator.instantiate(
                new ByteBuddyDispatcherInvocationHandler<>(
                        target,
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(ByteBuddyDeserializationProxyFactory.INSTANCE)
                ),
                interfaces);
    }
}
