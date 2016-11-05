package cz.novoj.generation.benchmark;

import cz.novoj.generation.contract.model.GenericBucketProxyGenerator;
import cz.novoj.generation.model.composite.CustomizedPerson;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JavassistGenericBucketProxyBenchmark {

	@State(Scope.Thread)
	public static class BenchmarkState {
		CustomizedPerson cus;

		@Setup(Level.Trial)
		public void doSetup() {
			cus = GenericBucketProxyGenerator.instantiateJavassistProxy(CustomizedPerson.class);
		}

	}

	@Benchmark
	public void instantiate(Blackhole blackhole) {
		final CustomizedPerson cus = GenericBucketProxyGenerator.instantiateJavassistProxy(CustomizedPerson.class);
		blackhole.consume(cus);
	}

	@Benchmark
	public void callMethod(BenchmarkState state) {
		state.cus.setFirstName("Jan");
	}

}
