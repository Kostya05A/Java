package coursehub.practice6.time;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// момент вебинара храним как Instant; зона нужна только для календарных расчётов
public final class WebinarSchedule {
  private final Instant start;

  public WebinarSchedule(Instant start) {
    if (start == null) {
      throw new IllegalArgumentException("start must not be null");
    }
    this.start = start;
  }

  public Instant start() {
    return start;
  }

  // Period: «один календарный день» — зависит от зоны и переходов времени
  public ZonedDateTime plusCalendarDays(int days, ZoneId zone) {
    return start.atZone(zone).plus(Period.ofDays(days));
  }

  // Duration: ровные 24 часа от момента — зона не участвует
  public Instant plusExactHours(long hours) {
    return start.plus(java.time.Duration.ofHours(hours));
  }

  // один и тот же Instant, показанный в конкретной зоне
  public ZonedDateTime displayIn(ZoneId zone) {
    return start.atZone(zone);
  }

  // показываем момент сразу в нескольких зонах, не меняя сам момент
  public java.util.List<String> displayAll(ZoneId... zones) {
    java.util.List<String> lines = new java.util.ArrayList<>();
    for (ZoneId zone : zones) {
      lines.add(zone + " -> " + displayIn(zone).toLocalDateTime());
    }
    return lines;
  }
}
