package cz.novoj.generation.dao;

import cz.novoj.generation.contract.dao.Dao;
import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPerson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
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
        final List<String> colNames = new LinkedList<>();
        IOUtils.readLines(is, "UTF-8")
                .forEach(s -> {
                    final String[] cols = s.split("\\|");
                    // process first line as header
                    if (colNames.isEmpty()) {
                        colNames.addAll(Arrays.asList(cols));
                    } else {
                        final CustomizedPerson person = createNew();
                        add(person);
                        for (int i = 0; i < colNames.size(); i++) {
                            final String colName = colNames.get(i);
                            if (cols.length > i) {
                                if (!cols[i].trim().isEmpty()) {
                                    if (colName.contains("Date")) {
                                        person.setProperty(
                                            colName,
                                            LocalDate.from(DateTimeFormatter.ISO_DATE.parse(cols[i]))
                                        );
                                    } else {
                                        person.setProperty(colName, cols[i]);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public abstract List<CustomizedPerson> getAll();

    public abstract List<CustomizedPerson> getAllSortedByFirstName();

    public abstract void add(CustomizedPerson person);

    public abstract void add(String firstName, String lastName, LocalDate birthDate);

    public abstract CustomizedPerson getByFirstNameAndLastName(String firstName, String lastName);

    public abstract CustomizedPerson getByAgeLessThanAndFirstNameEq(int age, String lastName);

    public abstract List<CustomizedPerson> getByAgeLessThan(int age);

    public abstract Optional<CustomizedPerson> getByFirstNameIsNullAndLastNameIsNotNull();

    public abstract List<CustomizedPerson> getByFirstNameInSortedByAgeDesc(String... firstNames);

    public abstract int removeByAge(int age);

    public abstract List<CustomizedPerson> removeAllByAge(int age);

}
