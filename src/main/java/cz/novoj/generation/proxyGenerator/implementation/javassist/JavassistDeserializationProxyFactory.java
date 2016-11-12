package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.contract.model.GenericBucket;
import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy.DeserializationProxyFactory;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public class JavassistDeserializationProxyFactory implements DeserializationProxyFactory<GenericBucket> {
	private static final long serialVersionUID = 3573491785842144918L;
	public static final SerializableProxy.DeserializationProxyFactory<GenericBucket> INSTANCE = new JavassistDeserializationProxyFactory();

	private JavassistDeserializationProxyFactory() {
        //singleton
    }

    @Override
    public Object deserialize(GenericBucket target, Class<?>[] interfaces) {
        return JavassistProxyGenerator.instantiate(
                new JavassistDispatcherInvocationHandler<>(
                        target,
                        GenericBucketProxyGenerator.getPropertiesInvoker(),
                        GenericBucketProxyGenerator.getterInvoker(),
                        GenericBucketProxyGenerator.setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(INSTANCE)
                ),
                interfaces);
    }
}
