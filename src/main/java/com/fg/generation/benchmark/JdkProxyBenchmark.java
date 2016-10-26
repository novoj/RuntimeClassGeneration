package com.fg.generation.benchmark;

import com.fg.generation.jdkProxy.JdkProxyGenerator;
import com.fg.generation.jdkProxy.invocationHandler.PassThroughInvocationHandler;
import com.fg.generation.model.composite.CustomizedPerson;
import com.fg.generation.model.composite.CustomizedPersonImpl;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JdkProxyBenchmark {

	@State(Scope.Thread)
	public static class BenchmarkState {
		CustomizedPerson cus;
		PassThroughInvocationHandler invocationHandler = new PassThroughInvocationHandler(new CustomizedPersonImpl());

		@Setup(Level.Trial)
		public void doSetup() {
			cus = JdkProxyGenerator.instantiate(CustomizedPerson.class, invocationHandler);
		}

	}

	@Benchmark
	public void instantiateProxy(Blackhole blackhole, BenchmarkState state) {
		final CustomizedPerson cus = JdkProxyGenerator.instantiate(CustomizedPerson.class, state.invocationHandler);
		blackhole.consume(cus);
	}

	@Benchmark
	public void callMethod(BenchmarkState state) {
		state.cus.setFirstName("Jan");
	}

}
