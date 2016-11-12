package cz.novoj.generation.proxyGenerator.infrastructure;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Helper class to work with JDK Date Time API and to allow setting fixed time in tests.
 */
public class ClockAccessor {
	private static final ClockAccessor INSTANCE = new ClockAccessor();
	private Clock currentClock;

	public static ClockAccessor getInstance() {
		return INSTANCE;
	}

	public static void setFixedTime(LocalDateTime time) {
		getInstance().currentClock = Clock.fixed(time.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
	}

	public Clock getCurrentClock() {
		return currentClock == null ? Clock.systemDefaultZone() : currentClock;
	}

	public LocalDate today() {
		return LocalDate.now(getCurrentClock());
	}

	public LocalDateTime now() {
		return LocalDateTime.now(getCurrentClock());
	}

}
