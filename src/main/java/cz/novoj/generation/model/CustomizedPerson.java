package cz.novoj.generation.model;

import cz.novoj.generation.model.traits.AgingPerson;
import cz.novoj.generation.model.traits.Person;
import cz.novoj.generation.model.traits.PropertyAccessor;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface CustomizedPerson extends Person, AgingPerson, PropertyAccessor {

	void doWork();

}
