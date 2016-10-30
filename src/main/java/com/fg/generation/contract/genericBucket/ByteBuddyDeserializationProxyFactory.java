package com.fg.generation.contract.genericBucket;

import com.fg.generation.bytebuddy.ByteBuddyDispatcherInvocationHandler;
import com.fg.generation.bytebuddy.BytebuddyProxyGenerator;
import com.fg.generation.infrastructure.SerializableProxy;

import java.util.Map;

import static com.fg.generation.contract.genericBucket.GenericBucketProxyGenerator.*;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
class ByteBuddyDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<Map<String, Object>> {
    static ByteBuddyDeserializationProxyFactory INSTANCE = new ByteBuddyDeserializationProxyFactory();

    private ByteBuddyDeserializationProxyFactory() {
        // singleton
    }

    @Override
    public Object deserialize(Map<String, Object> target, Class[] interfaces) {
        return BytebuddyProxyGenerator.instantiate(
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
