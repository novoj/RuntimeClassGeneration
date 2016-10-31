package com.fg.generation.benchmark;

import com.fg.generation.contract.GenericBucketProxyGenerator;
import com.fg.generation.model.composite.CustomizedPerson;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * No documentation needed, just look at the methods.
 *
 * @author Jan Novotný (novotny@fg.cz), FG Forrest a.s. (c) 2016
 */
public class JdkGenericBucketProxyBenchmark {

	@State(Scope.Thread)
	public static class BenchmarkState {
		CustomizedPerson cus;

		@Setup(Level.Trial)
		public void doSetup() {
			cus = GenericBucketProxyGenerator.instantiateJdkProxy(CustomizedPerson.class);
		}

	}

	@Benchmark
	public void instantiate(Blackhole blackhole) {
		final CustomizedPerson cus = GenericBucketProxyGenerator.instantiateJdkProxy(CustomizedPerson.class);
		blackhole.consume(cus);
	}

	@Benchmark
	public void callMethod(BenchmarkState state) {
		state.cus.setFirstName("Jan");
	}

}