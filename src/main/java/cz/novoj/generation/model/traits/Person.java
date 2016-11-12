package cz.novoj.generation.model.traits;


public interface Person extends PropertyAccessor {

	String getFirstName();
	void setFirstName(String firstName);

	String getLastName();
	void setLastName(String lastName);
	
}
