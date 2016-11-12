package cz.novoj.generation.proxyGenerator.implementation.cglib;

import net.sf.cglib.proxy.NoOp;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public interface CglibNoOpProxyGenerator {

    static <T> T instantiate(Class<T> contract) {
        return CglibProxyGenerator.instantiate(
				NoOp.INSTANCE,
				contract
		);
    }

}
