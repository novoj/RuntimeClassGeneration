package com.fg.generation.specific.jdkProxy;

import com.fg.generation.infrastructure.SerializableProxy;

import java.util.Map;

import static com.fg.generation.contract.GenericBucketProxyGenerator.*;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public class JdkProxyDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<Map<String, Object>> {
    public static JdkProxyDeserializationProxyFactory INSTANCE = new JdkProxyDeserializationProxyFactory();

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
