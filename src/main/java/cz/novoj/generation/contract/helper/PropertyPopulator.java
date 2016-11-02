package cz.novoj.generation.contract.helper;

import cz.novoj.generation.model.traits.PropertyAccessor;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
@Data
public class PropertyPopulator {
    private final List<BiConsumer<PropertyAccessor, Object[]>> populators;

    public PropertyPopulator(Method method) {
        populators = new LinkedList<>();
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter param = parameters[i];
            if (!param.isNamePresent()) {
                throw new IllegalStateException("Source code is not compiled with -parameters argument!");
            }
            final int argIndex = i;
            populators.add(((model, args) -> model.setProperty(param.getName(), args[argIndex])));
        }
    }

    public <T extends PropertyAccessor> T populate(T object, Object[] args) {
        populators.forEach(poupulator -> poupulator.accept(object, args));
        return object;
    }

}
