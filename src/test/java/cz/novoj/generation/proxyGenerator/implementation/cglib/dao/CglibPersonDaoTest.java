package cz.novoj.generation.proxyGenerator.implementation.cglib.dao;

import cz.novoj.generation.contract.dao.GenericBucketDaoProxyGenerator;
import cz.novoj.generation.dao.PersonDao;
import cz.novoj.generation.model.composite.CustomizedPerson;
import cz.novoj.generation.proxyGenerator.infrastructure.ClockAccessor;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;


@CommonsLog
public class CglibPersonDaoTest {
    private PersonDao personDao;

    @Before
    public void setUp() throws Exception {
        personDao = GenericBucketDaoProxyGenerator.instantiateCglibProxy(PersonDao.class, CustomizedPerson.class);

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("META-INF/data/persons.csv")) {
            personDao.loadFromCsv(is);
        }

		// fix date and time for tests
		ClockAccessor.setFixedTime(LocalDateTime.of(2016, 11, 5, 0, 0, 0, 0));
	}

	@Test
    public void PersonDao_GetAll_returnsAllPersons() throws Exception {
        assertEquals(500, personDao.getAll().size());
    }

	@Test
	public void PersonDao_AddNewPerson_returnsImportedPersonsPlusOne() throws Exception {
		personDao.add("Jan", "Novotný", LocalDate.of(1978, 5, 5));

		final List<CustomizedPerson> allPersons = personDao.getAll();
		assertEquals(501, allPersons.size());

		final CustomizedPerson lastPerson = allPersons.get(500);
		assertEquals("Jan", lastPerson.getFirstName());
		assertEquals("Novotný", lastPerson.getLastName());
	}

    @Test
    public void PersonDao_getByFirstNameAndLastName_returnsSinglePerson() throws Exception {
        final CustomizedPerson meredith = personDao.getByFirstNameAndLastName("Meredith", "Campbell");

        log.debug(meredith);

        assertNotNull(meredith);
        assertEquals("Meredith", meredith.getFirstName());
        assertEquals("Campbell", meredith.getLastName());
        assertEquals(LocalDate.of(2000, 5, 14), meredith.getBirthDate());
    }

    @Test
    public void PersonDao_getByAgeLessThan_returnsSinglePerson() throws Exception {
        final List<CustomizedPerson> nonMaturePersons = personDao.getByAgeLessThan(18);
        assertNotNull(nonMaturePersons);
        assertTrue(nonMaturePersons.size() > 0);

        for (CustomizedPerson nonMaturePerson : nonMaturePersons) {
            log.debug("Age " + nonMaturePerson.getAge() + ": " + nonMaturePerson);
            assertTrue(
                    nonMaturePerson + " was returned and has " + nonMaturePerson.getAge() + " years!",
                    nonMaturePerson.getAge() < 18
            );
        }
    }

    @Test
    public void PersonDao_getByAgeLessThanAndFirstNameEquals_returnsSinglePerson() throws Exception {
        CustomizedPerson delacruz;

        delacruz = personDao.getByAgeLessThanAndFirstNameEq(18, "Mary");
        assertNull(delacruz);

        delacruz = personDao.getByAgeLessThanAndFirstNameEq(40, "Mary");
        assertNotNull(delacruz);

        log.debug(delacruz);
    }

    @Test
    public void PersonDao_getAllSortedByFirstName_returnsSortedList() throws Exception {
        List<CustomizedPerson> personsSortedByFirstName = personDao.getAllSortedByFirstName();

        assertEquals(500, personsSortedByFirstName.size());
        CustomizedPerson previousPerson = null;
        for (CustomizedPerson person : personsSortedByFirstName) {
            log.debug(person);
            Integer comparationResult = ofNullable(previousPerson)
                    .map(previous -> previous.getFirstName().compareTo(person.getFirstName()))
                    .orElse(-1);
            assertTrue("Comparation returned " + comparationResult, comparationResult <= 0);
            previousPerson = person;
        }
    }

    @Test
    public void PersonDao_getByFirstNameIsNullLastNameNotIsNull_returnsSortedList() throws Exception {
        Optional<CustomizedPerson> personWithoutLastName = personDao.getByFirstNameIsNullAndLastNameIsNotNull();
        assertFalse("Returned " + personWithoutLastName.orElse(null), personWithoutLastName.isPresent());
    }

    @Test
    public void PersonDao_getByFirstNameInSortedByAgeDesc_returnsSortedList() throws Exception {
        List<CustomizedPerson> personsWithSelectedFirstName = personDao.getByFirstNameInSortedByAgeDesc("Linus", "Brian");

        assertEquals(3, personsWithSelectedFirstName.size());

        int previousAge = Integer.MAX_VALUE;
        for (CustomizedPerson person : personsWithSelectedFirstName) {
            log.debug(person);

            assertTrue(Arrays.asList("Linus", "Brian").contains(person.getFirstName()));
            assertTrue(previousAge > person.getAge());
        }
    }

    @Test
    public void PersonDao_removeByAge_removesItemsFromCollectionsAndReturnsNumberOfRemovedItems() throws Exception {
        int removedCount = personDao.removeByAge(0);

        assertEquals(494, removedCount);
        assertEquals(500 - removedCount, personDao.getAll().size());
    }

    @Test
    public void PersonDao_removeAllByAge_removesItemsFromCollectionsAndReturnsRemovedItems() throws Exception {
        List<CustomizedPerson> removedPersons = personDao.removeAllByAge(0);

        assertEquals(494, removedPersons.size());
        assertEquals(500 - removedPersons.size(), personDao.getAll().size());
    }

}