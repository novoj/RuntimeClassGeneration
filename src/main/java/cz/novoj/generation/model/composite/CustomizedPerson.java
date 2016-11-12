package cz.novoj.generation.model.composite;

import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.model.traits.AgingPerson;
import cz.novoj.generation.model.traits.Person;
import cz.novoj.generation.model.traits.Worker;


public interface CustomizedPerson extends Person, AgingPerson, Worker, PropertyAccessor {
}
