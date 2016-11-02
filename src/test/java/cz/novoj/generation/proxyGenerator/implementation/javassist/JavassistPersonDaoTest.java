package cz.novoj.generation.proxyGenerator.implementation.javassist;

import cz.novoj.generation.contract.DaoProxyGenerator;
import cz.novoj.generation.dao.PersonDao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
public class JavassistPersonDaoTest {
    private PersonDao personDao;

    @Before
    public void setUp() throws Exception {
        personDao = DaoProxyGenerator.instantiateJavassistProxy(PersonDao.class);
    }

    @Test
    public void PersonDao_CSVfile_isImported() throws Exception {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (java.io.InputStream is = classLoader.getResourceAsStream("META-INF/data/persons.csv")) {
            personDao.loadFromCsv(is);
        }

        assertEquals(500, personDao.getAll().size());
    }

}