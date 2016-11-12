package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import cz.novoj.generation.contract.model.GenericBucket;
import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.contract.model.SerializableProxy;
import cz.novoj.generation.contract.model.SerializableProxy.DeserializationProxyFactory;


public class JdkProxyDeserializationProxyFactory implements DeserializationProxyFactory<GenericBucket> {
	private static final long serialVersionUID = -3759232220601629153L;
	public static final DeserializationProxyFactory<GenericBucket> INSTANCE = new JdkProxyDeserializationProxyFactory();

    private JdkProxyDeserializationProxyFactory() {
        // singleton
    }

    @Override
    public Object deserialize(GenericBucket target, Class<?>[] interfaces) {
        return JdkProxyGenerator.instantiate(
                new JdkProxyDispatcherInvocationHandler<>(
                        target,
                        GenericBucketProxyGenerator.getPropertiesInvoker(),
                        GenericBucketProxyGenerator.getterInvoker(),
                        GenericBucketProxyGenerator.setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(INSTANCE)
                ),
                interfaces);
    }
}
