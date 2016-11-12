package cz.novoj.generation.proxyGenerator.implementation.cglib;

import net.sf.cglib.proxy.NoOp;


public interface CglibNoOpProxyGenerator {

    static <T> T instantiate(Class<T> contract) {
        return CglibProxyGenerator.instantiate(
				NoOp.INSTANCE,
				contract
		);
    }

}
