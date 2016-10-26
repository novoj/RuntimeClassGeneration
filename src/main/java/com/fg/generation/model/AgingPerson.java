package com.fg.generation.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface AgingPerson {

	LocalDate getBirthDate();
	void setBirthDate(LocalDate birthDate);

	default int getAge() {
		return (int)ChronoUnit.YEARS.between(getBirthDate(), LocalDate.now());
	}

}