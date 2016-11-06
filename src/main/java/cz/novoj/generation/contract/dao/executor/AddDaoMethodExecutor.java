package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.contract.dao.executor.helper.ReflectionUtils;
import cz.novoj.generation.contract.model.PropertyAccessor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by Rodina Novotnych on 02.11.2016.
 */
@Data
public class AddDaoMethodExecutor {
    private final List<BiConsumer<PropertyAccessor, Object[]>> populators;

    public AddDaoMethodExecutor(Method method) {
        populators = new LinkedList<>();
        final String[] parameterNames = ReflectionUtils.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            String parameterName = parameterNames[i];
            int finalI = i;
            populators.add(((model, args) -> model.setProperty(parameterName, args[finalI])));
        };

    }

    public <T extends PropertyAccessor> T populate(T object, Object[] args) {
        populators.forEach(poupulator -> poupulator.accept(object, args));
        return object;
    }

}
