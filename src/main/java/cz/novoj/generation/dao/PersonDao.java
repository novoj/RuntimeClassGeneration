package cz.novoj.generation.dao;

import cz.novoj.generation.contract.dao.Dao;
import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPerson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Created by Rodina Novotnych on 31.10.2016.
 */
public abstract class PersonDao implements Dao<CustomizedPerson> {

    @Override
    public Class<CustomizedPerson> getContractClass() {
        return CustomizedPerson.class;
    }

    @Override
    public CustomizedPerson createNew() {
        return GenericBucketProxyGenerator.instantiateJavassistProxy(getContractClass());
    }

    public void loadFromCsv(InputStream is) throws IOException {
        IOUtils.readLines(is, "UTF-8")
                .forEach(s -> {
                    String[] cols = s.split("\\|");
                    String firstName = cols[0];
                    String lastName = cols[1];
                    LocalDate birthDate = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(cols[2]));
                    add(
                        firstName.trim().isEmpty() ? null : firstName,
                        lastName.trim().isEmpty() ? null : lastName,
                        birthDate
                    );
                });
    }

    @Override
    public abstract List<CustomizedPerson> getAll();

    public abstract List<CustomizedPerson> getAllSortedByFirstName();

    public abstract void add(String firstName, String lastName, LocalDate birthDate);

    public abstract CustomizedPerson getByFirstNameAndLastName(String firstName, String lastName);

    public abstract CustomizedPerson getByAgeLessThanAndFirstNameEq(int age, String lastName);

    public abstract List<CustomizedPerson> getByAgeLessThan(int age);

    public abstract Optional<CustomizedPerson> getByFirstNameIsNullAndLastNameIsNotNull();

    public abstract List<CustomizedPerson> getByFirstNameInSortedByAgeDesc(String... firstNames);

}
