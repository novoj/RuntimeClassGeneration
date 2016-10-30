package com.fg.generation.contract.genericBucket;

import com.fg.generation.infrastructure.SerializableProxy;
import com.fg.generation.javassist.JavassistDispatcherInvocationHandler;
import com.fg.generation.javassist.JavassistProxyGenerator;

import java.util.Map;

import static com.fg.generation.contract.genericBucket.GenericBucketProxyGenerator.*;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
class JavassistDeserializationProxyFactory implements SerializableProxy.DeserializationProxyFactory<Map<String, Object>> {
    static JavassistDeserializationProxyFactory INSTANCE = new JavassistDeserializationProxyFactory();

    private JavassistDeserializationProxyFactory() {
        //singleton
    }

    @Override
    public Object deserialize(Map<String, Object> target, Class[] interfaces) {
        return JavassistProxyGenerator.instantiate(
                new JavassistDispatcherInvocationHandler<>(
                        target,
                        getPropertiesInvoker(),
                        getterInvoker(),
                        setterInvoker(),
                        SerializableProxy.getWriteReplaceMethodInvoker(JavassistDeserializationProxyFactory.INSTANCE)
                ),
                interfaces);
    }
}
