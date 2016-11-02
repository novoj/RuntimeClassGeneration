package cz.novoj.generation.model.traits;

import java.util.Map;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface PropertyAccessor {

	Object getProperty(String name);

	void setProperty(String name, Object value);

	Map<String, Object> getProperties();

}
