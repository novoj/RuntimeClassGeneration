package cz.novoj.generation.model.traits;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface Person extends PropertyAccessor {

	String getFirstName();
	void setFirstName(String firstName);

	String getLastName();
	void setLastName(String lastName);
	
}
