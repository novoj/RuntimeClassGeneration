package cz.novoj.generation.dao;

import cz.novoj.generation.model.traits.Person;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Rodina Novotnych on 31.10.2016.
 */
public abstract class PersonDao implements Dao<Person> {

    @Override
    public Class<Person> getContractClass() {
        return Person.class;
    }

    public void loadFromCsv(InputStream is) throws IOException {
        IOUtils.readLines(is, "UTF-8")
                .forEach(s -> {
                    String[] cols = s.split("|");
                    String firstName = cols[0];
                    String lastName = cols[1];
                    LocalDate birthDate = LocalDate.from(DateTimeFormatter.ISO_DATE.parse(cols[2]));
                    add(firstName, lastName, birthDate);
                });
    }

    public abstract void add(String firstName, String lastName, LocalDate birthDate);

    public abstract Person getByFirstNameAndLastName(String firstName, String lastName);

    public abstract List<Person> getByAge(int lessThan);

}
