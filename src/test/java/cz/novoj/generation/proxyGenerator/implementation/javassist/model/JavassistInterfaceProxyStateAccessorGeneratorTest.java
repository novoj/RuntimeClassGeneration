package cz.novoj.generation.proxyGenerator.implementation.javassist.model;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPerson;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.*;


public class JavassistInterfaceProxyStateAccessorGeneratorTest {

	@Test
	public void JavassistGenerator_Proxy_Created() throws Exception {
		CustomizedPerson person = GenericBucketProxyGenerator.instantiateJavassistProxy(CustomizedPerson.class);
		assertNotNull(person);
	}

	@Test
	public void JavassistGenerator_Proxy_GetPropertyReturnsSetValue() throws Exception {
		final CustomizedPerson person = createTestPersonProxy("Jan", "Novotný");

		assertEquals("Jan", person.getFirstName());
		assertEquals("Novotný", person.getLastName());
		assertEquals(LocalDate.of(1978, 5, 5), person.getBirthDate());
	}

	@Test
	public void JavassistGenerator_Proxy_DefaultMethodComputesAge() throws Exception {
		final CustomizedPerson person = createTestPersonProxy("Jan", "Novotný");

		assertEquals(38, person.getAge());
	}

	@Test
	public void JavassistGenerator_Proxy_GetPropertiesReturnsPopulatedMap() throws Exception {
		final CustomizedPerson person = createTestPersonProxy("Jan", "Novotný");

		final Map<String, Object> props = person.getProperties();
		assertEquals(3, props.size());
		assertTrue(props.containsKey("firstName"));
		assertTrue(props.containsKey("lastName"));
		assertTrue(props.containsKey("birthDate"));
	}

	@Test
	public void JavassistGenerator_Proxy_PropertiesCanBeSetIntoMap() throws Exception {
		final CustomizedPerson person = GenericBucketProxyGenerator.instantiateJavassistProxy(CustomizedPerson.class);
		final Map<String, Object> props = person.getProperties();
		props.put("firstName", "Jan");
		props.put("lastName", "Novotný");
		props.put("birthDate", LocalDate.of(1978, 5, 5));

		assertEquals("Jan", person.getFirstName());
		assertEquals("Novotný", person.getLastName());
		assertEquals(LocalDate.of(1978, 5, 5), person.getBirthDate());
	}

	@Test
	public void JavassistGenerator_Proxy_ToStringReturnsContentsOfTheMap() throws Exception {
		final CustomizedPerson person = createTestPersonProxy("Jan", "Novotný");

		assertEquals("{firstName=Jan, lastName=Novotný, birthDate=1978-05-05}", person.toString());
	}

	@Test
	public void JavassistGenerator_Proxy_HashCodeContractRespected() throws Exception {
		final CustomizedPerson person = createTestPersonProxy("Jan", "Novotný");
		final CustomizedPerson samePerson = createTestPersonProxy("Jan", "Novotný");

		assertNotSame(person, samePerson);
		assertEquals(person.hashCode(), samePerson.hashCode());
	}

	@Test
	public void JavassistGenerator_Proxy_EqualsContractRespected() throws Exception {
		final CustomizedPerson person = createTestPersonProxy("Jan", "Novotný");
		final CustomizedPerson samePerson = createTestPersonProxy("Jan", "Novotný");
		final CustomizedPerson differentPerson = createTestPersonProxy("Petr", "Novák");

		assertNotSame(person, samePerson);
		assertEquals(person, samePerson);
		assertNotEquals(person, differentPerson);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void JavassistGenerator_Proxy_NonhandledMethodThrowsException() throws Exception {
		final CustomizedPerson person = createTestPersonProxy("Jan", "Novotný");
		person.doWork();
	}

	private static CustomizedPerson createTestPersonProxy(String firstName, String lastName) {
		final CustomizedPerson person = GenericBucketProxyGenerator.instantiateJavassistProxy(CustomizedPerson.class);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setBirthDate(LocalDate.of(1978, 5, 5));
		return person;
	}

}