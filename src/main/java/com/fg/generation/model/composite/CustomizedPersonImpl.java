package com.fg.generation.model.composite;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@Data
public class CustomizedPersonImpl implements CustomizedPerson {
	private String firstName;
	private String lastName;
	private LocalDate birthDate;

	@Override
	public Map<String, Object> getProperties() {
		final Map<String, Object> props = new HashMap<>(3);
		props.put("firstName", firstName);
		props.put("lastName", lastName);
		props.put("birthDate", birthDate);
		return props;
	}

	@Override
	public void doWork() {
		// do nothing :)
	}
}
