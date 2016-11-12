package cz.novoj.generation.contract.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenericBucket implements Serializable {
	private static final long serialVersionUID = 4135508091866018653L;
	@Getter private final Map<String, Object> data = new LinkedHashMap<>(16);

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		GenericBucket that = (GenericBucket)o;
		return data.equals(that.data);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public String toString() {
		return data.toString();
	}

	public Object get(String propertyName) {
		return data.get(propertyName);
	}

	public void set(String propertyName, Object propertyValue) {
		data.put(propertyName, propertyValue);
	}
}
