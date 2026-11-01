package coursehub.practice6.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

// расписание серии вебинаров: моменты — Instant, календарь — в зоне организатора
public record WebinarSeriesPlan(
    CourseTitle title,
    Instant firstSession,
    ZoneId organizerZone,
    int sessionCount,
    Duration sessionDuration) {

  public WebinarSeriesPlan {
    if (title == null || firstSession == null || organizerZone == null || sessionDuration == null) {
      throw new IllegalArgumentException("required fields must not be null");
    }
    if (sessionCount <= 0) {
      throw new IllegalArgumentException("sessionCount must be > 0");
    }
    if (sessionDuration.isZero() || sessionDuration.isNegative()) {
      throw new IllegalArgumentException("sessionDuration must be positive");
    }
  }

  // сессии идут раз в неделю по календарю организатора — переходы DST учтены
  public List<Instant> sessionStarts() {
    List<Instant> starts = new ArrayList<>();
    ZonedDateTime session = firstSession.atZone(organizerZone);
    for (int i = 0; i < sessionCount; i++) {
      starts.add(session.toInstant());
      session = session.plus(Period.ofWeeks(1));
    }
    return starts;
  }

  // сколько занятий ещё предстоит после указанного момента
  public int remainingSessions(Instant moment) {
    int remaining = 0;
    for (Instant start : sessionStarts()) {
      if (start.isAfter(moment)) {
        remaining++;
      }
    }
    return remaining;
  }

  // местное время начала для пользователя в другой зоне
  public ZonedDateTime firstSessionFor(ZoneId zone) {
    return firstSession.atZone(zone);
  }

  public boolean containsLeapDay() {
    for (Instant start : sessionStarts()) {
      LocalDate date = start.atZone(organizerZone).toLocalDate();
      if (date.getMonthValue() == 2 && date.getDayOfMonth() == 29) {
        return true;
      }
    }
    return false;
  }
}
