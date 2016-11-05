package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;

import java.util.Map;

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
                        GenericBucketProxyGenerator.getPropertiesInvoker(),
                        GenericBucketProxyGenerator.getterInvoker(),
                        GenericBucketProxyGenerator.setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JdkProxyDeserializationProxyFactory.INSTANCE)
                ),
                interfaces);
    }
}
