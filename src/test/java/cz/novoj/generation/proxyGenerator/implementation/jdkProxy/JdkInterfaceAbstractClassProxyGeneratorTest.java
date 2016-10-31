package cz.novoj.generation.proxyGenerator.implementation.jdkProxy;

import cz.novoj.generation.contract.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPersonAbstract;
import org.junit.Test;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JdkInterfaceAbstractClassProxyGeneratorTest {

	@Test(expected = IllegalArgumentException.class)
	public void JavassistGenerator_ProxyAbstract_Created() throws Exception {
		GenericBucketProxyGenerator.instantiateJdkProxy(CustomizedPersonAbstract.class);
	}

}