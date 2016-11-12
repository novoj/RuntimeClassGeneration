package cz.novoj.generation.benchmark;

import cz.novoj.generation.model.composite.CustomizedPerson;
import cz.novoj.generation.proxyGenerator.implementation.cglib.CglibNoOpProxyGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;


public class CglibNoOpProxyBenchmark {

	@State(Scope.Thread)
	public static class BenchmarkState {
		CustomizedPerson cus;

		@Setup(Level.Trial)
		public void doSetup() {
			cus = CglibNoOpProxyGenerator.instantiate(CustomizedPerson.class);
		}

	}

	@Benchmark
	public void instantiate(Blackhole blackhole) {
		final CustomizedPerson cus = CglibNoOpProxyGenerator.instantiate(CustomizedPerson.class);
		blackhole.consume(cus);
	}

	@Benchmark
	public void callMethod(BenchmarkState state) {
		state.cus.setFirstName("Jan");
	}

}
