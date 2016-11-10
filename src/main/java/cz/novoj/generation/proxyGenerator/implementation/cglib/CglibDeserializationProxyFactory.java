package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;

import java.util.Map;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public class CglibDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<Map<String, Object>> {
    public static CglibDeserializationProxyFactory INSTANCE = new CglibDeserializationProxyFactory();

    private CglibDeserializationProxyFactory() {
        //singleton
    }

    @Override
    public Object deserialize(Map<String, Object> target, Class[] interfaces) {
        return CglibProxyGenerator.instantiate(
                new CglibDispatcherInvocationHandler<>(
                        target,
                        GenericBucketProxyGenerator.getPropertiesInvoker(),
                        GenericBucketProxyGenerator.getterInvoker(),
                        GenericBucketProxyGenerator.setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(CglibDeserializationProxyFactory.INSTANCE)
                ),
                interfaces);
    }
}
