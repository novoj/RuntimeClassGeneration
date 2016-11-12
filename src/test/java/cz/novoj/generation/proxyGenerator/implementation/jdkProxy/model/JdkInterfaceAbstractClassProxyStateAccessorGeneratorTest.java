package cz.novoj.generation.proxyGenerator.implementation.jdkProxy.model;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPersonAbstract;
import org.junit.Test;


public class JdkInterfaceAbstractClassProxyStateAccessorGeneratorTest {

	@Test(expected = IllegalArgumentException.class)
	public void JdkProxyGenerator_ProxyAbstract_Created() throws Exception {
		GenericBucketProxyGenerator.instantiateJdkProxy(CustomizedPersonAbstract.class);
	}

}