package cz.novoj.generation.benchmark;

import cz.novoj.generation.model.composite.CustomizedPerson;
import cz.novoj.generation.proxyGenerator.implementation.jdkProxy.JdkNoOpProxyGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;


public class JdkNoOpProxyBenchmark {

	@State(Scope.Thread)
	public static class BenchmarkState {
		CustomizedPerson cus;

		@Setup(Level.Trial)
		public void doSetup() {
			cus = JdkNoOpProxyGenerator.instantiate(CustomizedPerson.class);
		}

	}

	@Benchmark
	public void instantiate(Blackhole blackhole) {
		final CustomizedPerson cus = JdkNoOpProxyGenerator.instantiate(CustomizedPerson.class);
		blackhole.consume(cus);
	}

	@Benchmark
	public void callMethod(BenchmarkState state) {
		state.cus.setFirstName("Jan");
	}

}
