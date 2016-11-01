package cz.novoj.generation.proxyGenerator.implementation.bytebuddy;

import cz.novoj.generation.contract.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPersonAbstract;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class ByteBuddyAbstractClassProxyGeneratorTest {

	@Test
	public void ByteBuddyGenerator_ProxyAbstract_Created() throws Exception {
		assertNotNull(GenericBucketProxyGenerator.instantiateByteBuddyProxy(CustomizedPersonAbstract.class));
	}

}