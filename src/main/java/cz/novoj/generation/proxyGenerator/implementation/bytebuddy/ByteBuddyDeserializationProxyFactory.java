package cz.novoj.generation.proxyGenerator.implementation.bytebuddy;

import cz.novoj.generation.contract.model.GenericBucket;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy;
import cz.novoj.generation.proxyGenerator.infrastructure.SerializableProxy.DeserializationProxyFactory;

import static cz.novoj.generation.contract.model.GenericBucketProxyGenerator.getPropertiesInvoker;
import static cz.novoj.generation.contract.model.GenericBucketProxyGenerator.getterInvoker;
import static cz.novoj.generation.contract.model.GenericBucketProxyGenerator.setterInvoker;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public class ByteBuddyDeserializationProxyFactory implements DeserializationProxyFactory<GenericBucket> {
	private static final long serialVersionUID = -9030607883489527280L;
	public static final DeserializationProxyFactory<GenericBucket> INSTANCE = new ByteBuddyDeserializationProxyFactory();

	private ByteBuddyDeserializationProxyFactory() {
        // singleton
    }

    @Override
    public Object deserialize(GenericBucket target, Class<?>[] interfaces) {
        return ByteBuddyProxyGenerator.instantiate(
                new ByteBuddyDispatcherInvocationHandler<>(
                        target,
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(INSTANCE)
                ),
                interfaces);
    }
}
