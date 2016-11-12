package cz.novoj.generation.contract.dao.executor;

import cz.novoj.generation.proxyGenerator.infrastructure.ReflectionUtils;
import cz.novoj.generation.model.traits.PropertyAccessor;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * This implementation will examine method parameters and creates list of functions that sets property to the repository
 * item. Property will be named according to method parameter name and will be filled with value taken from the real
 * invocation arguments from the position that corresponds with parameter name position.
 *
 * This functionality requires code to be compiled with "-parameters" argument.
 *
 * @param <T>
 */
public class AddDaoMethodExecutor<T extends PropertyAccessor> implements DaoMethodExecutor<T> {
    private final List<BiConsumer<PropertyAccessor, Object[]>> populateFcts = new LinkedList<>();

	/**
	 * Constructor is called once per method - this object represents method context, that is cached between invocations.
	 * Ie. it can be fairly expensive and it doesn't matter much.
	 *
	 * @param method
	 */
	public AddDaoMethodExecutor(Method method) {
    	// get parameter names in order in which are placed on method
        final String[] parameterNames = ReflectionUtils.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            final String parameterName = parameterNames[i];
            final int parameterPosition = i;
            // for each parameter register lambda with parameterName and arg position baked in
            populateFcts.add((model, args) -> model.setProperty(parameterName, args[parameterPosition]));
        }
    }

	/**
	 * This method is called on each method invocation and is optimized for speed.
	 * It simply iterates over populate functions and calls them with proxyState (ie. repository item) and actual
	 * invocation arguments.
	 *
	 * @param proxyState
	 * @param args
	 * @return
	 */
	@Override
	public T apply(T proxyState, Object... args) {
		populateFcts.forEach(poupulator -> poupulator.accept(proxyState, args));
		return proxyState;
	}

}
