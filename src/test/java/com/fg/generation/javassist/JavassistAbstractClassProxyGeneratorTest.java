package com.fg.generation.javassist;

import com.fg.generation.contract.GenericBucketProxyGenerator;
import com.fg.generation.model.composite.CustomizedPerson;
import com.fg.generation.model.composite.CustomizedPersonAbstract;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JavassistAbstractClassProxyGeneratorTest {

	@Test
	public void JavassistGenerator_ProxyAbstract_Created() throws Exception {
		CustomizedPerson person = GenericBucketProxyGenerator.instantiateJavassistProxy(CustomizedPersonAbstract.class);
		assertNotNull(person);
	}

	@Test
	public void JavassistGenerator_ProxyAbstract_InvokesRealMethodUsingAbstractOnes() throws Exception {
		final CustomizedPersonAbstract person = createTestPersonProxy("Jan", "Novotný");
		assertEquals("Jan Novotný", person.getCompleteName());
	}

	private static CustomizedPersonAbstract createTestPersonProxy(String firstName, String lastName) {
		final CustomizedPersonAbstract person = GenericBucketProxyGenerator.instantiateJavassistProxy(CustomizedPersonAbstract.class);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		return person;
	}

}