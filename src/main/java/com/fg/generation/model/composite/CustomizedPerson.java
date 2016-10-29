package com.fg.generation.model.composite;

import com.fg.generation.model.AgingPerson;
import com.fg.generation.model.Person;
import com.fg.generation.model.PropertyAccessor;
import com.fg.generation.model.Worker;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface CustomizedPerson extends Person, AgingPerson, Worker, PropertyAccessor {
}
