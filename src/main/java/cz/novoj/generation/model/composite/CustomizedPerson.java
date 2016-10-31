package cz.novoj.generation.model.composite;

import cz.novoj.generation.model.traits.AgingPerson;
import cz.novoj.generation.model.traits.Person;
import cz.novoj.generation.model.traits.PropertyAccessor;
import cz.novoj.generation.model.traits.Worker;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface CustomizedPerson extends Person, AgingPerson, Worker, PropertyAccessor {
}
