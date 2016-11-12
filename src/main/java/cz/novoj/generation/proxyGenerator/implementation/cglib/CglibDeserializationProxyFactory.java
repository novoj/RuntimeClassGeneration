package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.contract.model.GenericBucket;
import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy.DeserializationProxyFactory;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public class CglibDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<GenericBucket> {
	private static final long serialVersionUID = 9041699137593106730L;
	public static final DeserializationProxyFactory<GenericBucket> INSTANCE = new CglibDeserializationProxyFactory();

	private CglibDeserializationProxyFactory() {
        //singleton
    }

    @Override
    public Object deserialize(GenericBucket target, Class<?>[] interfaces) {
        return CglibProxyGenerator.instantiate(
                new CglibDispatcherInvocationHandler<>(
                        target,
                        GenericBucketProxyGenerator.getPropertiesInvoker(),
                        GenericBucketProxyGenerator.getterInvoker(),
                        GenericBucketProxyGenerator.setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(INSTANCE)
                ),
                interfaces);
    }
}
