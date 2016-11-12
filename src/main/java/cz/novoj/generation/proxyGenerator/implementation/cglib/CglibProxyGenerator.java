package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.proxyGenerator.infrastructure.Proxy;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@CommonsLog
public final class
CglibProxyGenerator {

    public static <T> T instantiate(Callback callback, Class<?>... interfaces) {
		Enhancer f = new Enhancer();
		if (interfaces[0].isInterface()) {
			final Class[] finalContract = new Class[interfaces.length + 1];
			finalContract[0] = Proxy.class;
			System.arraycopy(interfaces, 0, finalContract, 1, interfaces.length);

			f.setInterfaces(finalContract);
		} else {
			final Class[] finalContract = new Class[interfaces.length];
			finalContract[0] = Proxy.class;
			System.arraycopy(interfaces, 1, finalContract, 1, interfaces.length - 1);

			f.setSuperclass(interfaces[0]);
			f.setInterfaces(finalContract);
		}

		f.setCallbackFilter(method -> "finalize".equals(method.getName()) && method.getParameterCount() == 0 ? 0 : 1);
		f.setCallback(callback);

		Class proxyClass = f.createClass();
		log.info("Created proxy class: " + proxyClass.getName());
		try {
			return (T)proxyClass.newInstance();
		} catch(IllegalAccessException | InstantiationException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
