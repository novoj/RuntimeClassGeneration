package cz.novoj.generation.proxyGenerator.implementation.cglib;

import cz.novoj.generation.proxyGenerator.infrastructure.ProxyStateAccessor;
import lombok.extern.apachecommons.CommonsLog;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
@CommonsLog
public class CglibProxyGenerator {

    @SuppressWarnings("unchecked")
	public static <T> T instantiate(Callback callback, Class<?>... interfaces) {
    	// WE CAN'T EASILY CACHE THE CLASS, CGLIB HAS NO WAY HOW TO PASS DIFFERENT CALLBACK INSTANCE TO EACH INSTANCE
		// WE HAVE TO DELEGATE CACHING TO THE CGLIB ITSELF
		final Enhancer fct = new Enhancer();

		// IF WE PROXY ABSTRACT CLASS, WE HAVE A RULE THAT IT HAS TO BE FIRST IN LIST
		if (interfaces[0].isInterface()) {
			// FIRST IS INTERFACE
			// AUTOMATICALLY ADD PROXYSTATEACCESSOR CLASS TO EVERY OUR PROXY WE CREATE
			final Class<?>[] finalContract = new Class[interfaces.length + 1];
			finalContract[0] = ProxyStateAccessor.class;
			System.arraycopy(interfaces, 0, finalContract, 1, interfaces.length);
			// WE'LL EXTEND OBJECT CLASS AND IMPLEMENT ALL INTERFACES
			fct.setInterfaces(finalContract);
		} else {
			// FIRST IS ABSTRACT CLASS
			// AUTOMATICALLY ADD PROXYSTATEACCESSOR CLASS TO EVERY OUR PROXY WE CREATE
			final Class<?>[] finalContract = new Class[interfaces.length];
			finalContract[0] = ProxyStateAccessor.class;
			System.arraycopy(interfaces, 1, finalContract, 1, interfaces.length - 1);
			// WE'LL EXTEND ABSTRACT CLASS AND IMPLEMENT ALL OTHER INTERFACES
			fct.setSuperclass(interfaces[0]);
			fct.setInterfaces(finalContract);
		}

		// SKIP FINALIZE METHOD OVERRIDE - STAY AWAY FROM TROUBLE :)
		fct.setCallbackFilter(method -> "finalize".equals(method.getName()) && method.getParameterCount() == 0 ? 0 : 1);
		// SET CALLBACK TO THE NEWLY CREATED INSTANCE
		fct.setCallback(callback);

		Class<T> proxyClass = fct.createClass();
		log.info("Created proxy class: " + proxyClass.getName());
		try {

			// CREATE INSTANCE
			return proxyClass.getConstructor().newInstance();

		} catch(Exception e) {
			throw new IllegalArgumentException("What the heck? Can't create proxy: " + e.getMessage(), e);
		}
	}

}
