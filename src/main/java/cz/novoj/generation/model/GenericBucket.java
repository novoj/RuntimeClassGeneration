package cz.novoj.generation.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
public class GenericBucket extends HashMap<String, Object> {

    public GenericBucket(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public GenericBucket(int initialCapacity) {
        super(initialCapacity);
    }

    public GenericBucket() {
    }

    public GenericBucket(Map<? extends String, ?> m) {
        super(m);
    }

}
