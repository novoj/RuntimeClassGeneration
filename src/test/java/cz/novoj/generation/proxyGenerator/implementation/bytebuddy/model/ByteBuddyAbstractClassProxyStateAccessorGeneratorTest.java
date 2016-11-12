package cz.novoj.generation.proxyGenerator.implementation.bytebuddy.model;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPerson;
import cz.novoj.generation.model.composite.CustomizedPersonAbstract;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ByteBuddyAbstractClassProxyStateAccessorGeneratorTest {

	@Test
	public void ByteBuddyGenerator_ProxyAbstract_Created() throws Exception {
		CustomizedPerson person = GenericBucketProxyGenerator.instantiateByteBuddyProxy(CustomizedPersonAbstract.class);
		assertNotNull(person);
	}

	@Test
	public void ByteBuddyGenerator_ProxyAbstract_InvokesRealMethodUsingAbstractOnes() throws Exception {
		final CustomizedPersonAbstract person = createTestPersonProxy("Jan", "Novotný");
		assertEquals("Jan Novotný", person.getCompleteName());
	}

	private static CustomizedPersonAbstract createTestPersonProxy(String firstName, String lastName) {
		final CustomizedPersonAbstract person = GenericBucketProxyGenerator.instantiateByteBuddyProxy(CustomizedPersonAbstract.class);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		return person;
	}

}