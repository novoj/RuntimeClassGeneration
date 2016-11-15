package cz.novoj.generation.contract.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("InstanceMethodNamingConvention")
@EqualsAndHashCode
public class GenericBucket {
	@Getter private final Map<String, Object> data = new LinkedHashMap<>(16);

	public Object get(String propertyName) {
		return data.get(propertyName);
	}

	public void set(String propertyName, Object propertyValue) {
		data.put(propertyName, propertyValue);
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
