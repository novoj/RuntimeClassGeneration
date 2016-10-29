package com.fg.generation.model.composite;

import static java.util.Optional.ofNullable;

/**
 * Created by Rodina Novotnych on 29.10.2016.
 */
public abstract class CustomizedPersonAbstract implements CustomizedPerson {

    public String getCompleteName() {
        return ofNullable(getFirstName()).map(it -> it + " ").orElse("") +
                ofNullable(getLastName()).orElse("");
    }

}
