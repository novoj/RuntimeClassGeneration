package cz.novoj.generation.model;

import java.util.Map;


public interface PropertyAccessor {

	Object getProperty(String name);

	void setProperty(String name, Object value);

	Map<String, Object> getProperties();

}
