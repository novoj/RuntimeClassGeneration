package cz.novoj.generation.model;

import cz.novoj.generation.model.traits.AgingPerson;
import cz.novoj.generation.model.traits.Person;

import static java.util.Optional.ofNullable;

public abstract class CustomizedPerson implements Person, AgingPerson {

    public String getCompleteName() {
        return ofNullable(getFirstName()).map(it -> it + ' ').orElse("") +
                ofNullable(getLastName()).orElse("");
    }

}
