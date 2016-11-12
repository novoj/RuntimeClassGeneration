package cz.novoj.generation.contract.model;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class GenericBucket {
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

}
