package coursehub.practice6.time;

import java.time.LocalDate;
import java.time.Year;

// календарные расчёты: LocalDate неизменяем — результат операции нужно присвоить
public final class StudyCalendar {
  private final LocalDate start;

  public StudyCalendar(LocalDate start) {
    if (start == null) {
      throw new IllegalArgumentException("start must not be null");
    }
    this.start = start;
  }

  public LocalDate start() {
    return start;
  }

  public boolean isLeapYear() {
    return Year.isLeap(start.getYear());
  }

  // даты начала модулей: первый модуль со start, дальше с шагом в неделях
  public java.util.List<LocalDate> moduleStarts(int modules, int moduleWeeks) {
    java.util.List<LocalDate> starts = new java.util.ArrayList<>();
    LocalDate date = start;
    for (int i = 0; i < modules; i++) {
      starts.add(date);
      // plusWeeks не меняет date, поэтому результат присваиваем обратно
      date = date.plusWeeks(moduleWeeks);
    }
    return starts;
  }

  // последний учебный день: конец последней недели, сдвинутый на будний день
  public LocalDate lastStudyDay(int totalWeeks) {
    LocalDate end = start.plusWeeks(totalWeeks).minusDays(1);
    while (end.getDayOfWeek() == java.time.DayOfWeek.SATURDAY
        || end.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
      end = end.minusDays(1);
    }
    return end;
  }
}
