package com.fg.generation.contract.genericBucket;

import com.fg.generation.infrastructure.SerializableProxy;
import com.fg.generation.jdkProxy.JdkProxyDispatcherInvocationHandler;
import com.fg.generation.jdkProxy.JdkProxyGenerator;

import java.util.Map;

import static com.fg.generation.contract.genericBucket.GenericBucketProxyGenerator.*;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
class JdkProxyDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<Map<String, Object>> {
    static JdkProxyDeserializationProxyFactory INSTANCE = new JdkProxyDeserializationProxyFactory();

    private JdkProxyDeserializationProxyFactory() {
        // singleton
    }

    @Override
    public Object deserialize(Map<String, Object> target, Class[] interfaces) {
        return JdkProxyGenerator.instantiate(
                new JdkProxyDispatcherInvocationHandler<>(
                        target,
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JdkProxyDeserializationProxyFactory.INSTANCE)
                ),
                interfaces);
    }
}
