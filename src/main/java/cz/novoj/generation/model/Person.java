package cz.novoj.generation.model;

public interface Person extends PropertyAccessor {

	String getFirstName();
	void setFirstName(String firstName);

	String getLastName();
	void setLastName(String lastName);
	
}
