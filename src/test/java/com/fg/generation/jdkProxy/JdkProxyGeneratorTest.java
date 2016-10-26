package com.fg.generation.jdkProxy;

import com.fg.generation.jdkProxy.invocationHandler.GenericBucketInvocationHandler;
import com.fg.generation.model.composite.CustomizedPerson;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JdkProxyGeneratorTest {

	@Test
	public void JdkProxyGenerator_CustomizedPerson_ProxyCreated() throws Exception {
		final CustomizedPerson person = JdkProxyGenerator.instantiate(CustomizedPerson.class, new GenericBucketInvocationHandler());
		person.setFirstName("Jan");
		person.setLastName("Novotný");
		person.setBirthDate(LocalDate.of(1978, 5, 5));

		assertEquals("Jan", person.getFirstName());
		assertEquals("Novotný", person.getLastName());
		assertEquals(LocalDate.of(1978, 5, 5), person.getBirthDate());
		assertEquals(38, person.getAge());

		final Map<String, Object> props = person.getProperties();
		assertEquals(3, props.size());
		assertTrue(props.containsKey("firstName"));
		assertTrue(props.containsKey("lastName"));
		assertTrue(props.containsKey("birthDate"));

		props.put("firstName", "Petr");
		assertEquals("Petr", person.getFirstName());
	}


}