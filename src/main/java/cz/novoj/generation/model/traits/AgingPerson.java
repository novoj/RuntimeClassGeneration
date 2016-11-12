package cz.novoj.generation.model.traits;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public interface AgingPerson {

	LocalDate getBirthDate();
	void setBirthDate(LocalDate birthDate);

	default Integer getAge() {
		return Math.toIntExact(ChronoUnit.YEARS.between(getBirthDate(), LocalDate.now()));
	}

}
