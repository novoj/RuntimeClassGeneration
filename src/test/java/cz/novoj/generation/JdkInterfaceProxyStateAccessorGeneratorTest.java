package cz.novoj.generation;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.Person;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;


public class JdkInterfaceProxyStateAccessorGeneratorTest {

	@Test
	public void JdkProxyGenerator_Proxy_Created() throws Exception {
		Person person = GenericBucketProxyGenerator.instantiate(Person.class);
		assertNotNull(person);
	}

	@Test
	public void JdkProxyGenerator_Proxy_GetPropertyReturnsSetValue() throws Exception {
        final Person person = createTestPersonProxy("Jan", "Novotný");

		assertEquals("Jan", person.getFirstName());
		assertEquals("Novotný", person.getLastName());
	}

	@Test
	public void JdkProxyGenerator_Proxy_GetPropertiesReturnsPopulatedMap() throws Exception {
        final Person person = createTestPersonProxy("Jan", "Novotný");

		final Map<String, Object> props = person.getProperties();
		assertEquals(2, props.size());
		assertTrue(props.containsKey("firstName"));
		assertTrue(props.containsKey("lastName"));
	}

	@Test
	public void JdkProxyGenerator_Proxy_PropertiesCanBeSetIntoMap() throws Exception {
		final Person person = GenericBucketProxyGenerator.instantiate(Person.class);
		final Map<String, Object> props = person.getProperties();
		props.put("firstName", "Jan");
		props.put("lastName", "Novotný");

		assertEquals("Jan", person.getFirstName());
		assertEquals("Novotný", person.getLastName());
	}

	@Test
	public void JdkProxyGenerator_Proxy_ToStringReturnsContentsOfTheMap() throws Exception {
        final Person person = createTestPersonProxy("Jan", "Novotný");

		assertEquals("{firstName=Jan, lastName=Novotný}", person.toString());
	}

    @Test
    public void JdkProxyGenerator_Proxy_HashCodeContractRespected() throws Exception {
        final Person person = createTestPersonProxy("Jan", "Novotný");
        final Person samePerson = createTestPersonProxy("Jan", "Novotný");

        assertNotSame(person, samePerson);
        assertEquals(person.hashCode(), samePerson.hashCode());
    }

    @Test
    public void JdkProxyGenerator_Proxy_EqualsContractRespected() throws Exception {
        final Person person = createTestPersonProxy("Jan", "Novotný");
        final Person samePerson = createTestPersonProxy("Jan", "Novotný");
        final Person differentPerson = createTestPersonProxy("Petr", "Novák");

        assertNotSame(person, samePerson);
        assertEquals(person, samePerson);
        assertNotEquals(person, differentPerson);
    }

    private Person createTestPersonProxy(String firstName, String lastName) {
        final Person person = GenericBucketProxyGenerator.instantiate(Person.class);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        return person;
    }

}