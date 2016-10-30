package com.fg.generation.jdkProxy;

import com.fg.generation.contract.genericBucket.GenericBucketProxyGenerator;
import com.fg.generation.model.composite.CustomizedPersonAbstract;
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