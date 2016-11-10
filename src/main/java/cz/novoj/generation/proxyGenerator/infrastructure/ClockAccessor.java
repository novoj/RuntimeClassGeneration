package cz.novoj.generation.proxyGenerator.infrastructure;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

	public void setCurrentClock(Clock currentClock) {
		this.currentClock = currentClock;
	}

	public LocalDate today() {
		return LocalDate.now(getCurrentClock());
	}

	public LocalDate today(ZoneId zoneId) {
		return currentClock == null && zoneId != null ? LocalDate.now(zoneId) : today();
	}

	public LocalDateTime now() {
		return LocalDateTime.now(getCurrentClock());
	}

	public LocalDateTime now(ZoneId zoneId) {
		return currentClock == null && zoneId != null ? LocalDateTime.now(zoneId) : now();
	}

	public LocalDateTime nextMinute() {
		return LocalDateTime.now(getCurrentClock()).withSecond(0).withNano(0).plusMinutes(1);
	}

	public LocalDateTime lastMinute() {
		return LocalDateTime.now(getCurrentClock()).withSecond(0).withNano(0).minusMinutes(1);
	}

}
