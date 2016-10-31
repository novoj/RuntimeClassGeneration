package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.contract.GenericBucketProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;

import java.util.Map;

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
                        GenericBucketProxyGenerator.getPropertiesInvoker(),
                        GenericBucketProxyGenerator.getterInvoker(),
                        GenericBucketProxyGenerator.setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JavassistDeserializationProxyFactory.INSTANCE)
                ),
                interfaces);
    }
}
