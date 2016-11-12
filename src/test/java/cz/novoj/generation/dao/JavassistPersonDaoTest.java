package cz.novoj.generation.dao;

import cz.novoj.generation.contract.dao.GenericBucketDaoProxyGenerator;
import cz.novoj.generation.model.CustomizedPerson;
import cz.novoj.generation.proxyGenerator.infrastructure.ClockAccessor;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

@CommonsLog
public class JavassistPersonDaoTest {
    private PersonDao personDao;

    @Before
    public void setUp() throws Exception {
        personDao = GenericBucketDaoProxyGenerator.instantiateJavassistProxy(PersonDao.class, CustomizedPerson.class);

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

}