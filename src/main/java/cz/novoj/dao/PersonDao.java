package cz.novoj.dao;

import cz.novoj.generation.model.composite.CustomizedPerson;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Created by Rodina Novotnych on 31.10.2016.
 */
public abstract class PersonDao {

    public abstract CustomizedPerson createNew();

    public abstract void add(CustomizedPerson person);

    public abstract void add(String firstName, String lastName, LocalDate birthDate);

    public abstract List<CustomizedPerson> getAll();

    public abstract CustomizedPerson[] getAllSortedByFirstName();

    public abstract List<CustomizedPerson> getByAgeLessThan(int age);

    public abstract CustomizedPerson getByFirstNameAndLastName(String firstName, String lastName);

    public abstract Optional<CustomizedPerson> getByFirstNameIsNullAndLastNameIsNotNull();

    public abstract List<CustomizedPerson> getByFirstNameInSortedByAgeDesc(String... firstNames);

    public abstract int removeByAge(int age);

    public abstract List<CustomizedPerson> removeAllByAge(int age);

}
