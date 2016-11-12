package cz.novoj.generation.model.traits;

import cz.novoj.generation.proxyGenerator.infrastructure.ClockAccessor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public interface AgingPerson {

	LocalDate getBirthDate();
	void setBirthDate(LocalDate birthDate);

	default int getAge() {
		return (int)ChronoUnit.YEARS.between(getBirthDate(), ClockAccessor.getInstance().now());
	}

}
