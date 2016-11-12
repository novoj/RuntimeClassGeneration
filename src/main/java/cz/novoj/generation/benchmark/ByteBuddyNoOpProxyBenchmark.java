package cz.novoj.generation.benchmark;

import cz.novoj.generation.model.composite.CustomizedPerson;
import cz.novoj.generation.proxyGenerator.implementation.bytebuddy.ByteBuddyNoOpProxyGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;


public class ByteBuddyNoOpProxyBenchmark {

	@State(Scope.Thread)
	public static class BenchmarkState {
		CustomizedPerson cus;

		@Setup(Level.Trial)
		public void doSetup() {
			cus = ByteBuddyNoOpProxyGenerator.instantiate(CustomizedPerson.class);
		}

	}

	@Benchmark
	public void instantiate(Blackhole blackhole) {
		final CustomizedPerson cus = ByteBuddyNoOpProxyGenerator.instantiate(CustomizedPerson.class);
		blackhole.consume(cus);
	}

	@Benchmark
	public void callMethod(BenchmarkState state) {
		state.cus.setFirstName("Jan");
	}

}
