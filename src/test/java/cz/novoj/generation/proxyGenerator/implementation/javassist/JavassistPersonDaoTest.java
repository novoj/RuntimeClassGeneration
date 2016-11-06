package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.contract.dao.DaoProxyGenerator;
import cz.novoj.generation.dao.PersonDao;
import cz.novoj.generation.model.composite.CustomizedPerson;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Optional.ofNullable;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
@CommonsLog
public class JavassistPersonDaoTest {
    private PersonDao personDao;

    @Before
    public void setUp() throws Exception {
        personDao = DaoProxyGenerator.instantiateJavassistProxy(PersonDao.class);

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (java.io.InputStream is = classLoader.getResourceAsStream("META-INF/data/persons.csv")) {
            personDao.loadFromCsv(is);
        }
    }

    @Test
    public void PersonDao_GetAll_returnsAllPersons() throws Exception {
        assertEquals(500, personDao.getAll().size());
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
}