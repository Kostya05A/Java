package coursehub.practice6.time;

import java.time.LocalDate;

// исходный объект не меняется: start остаётся тем же после всех расчётов
public class StudyCalendarDemo {
  public static void main(String[] args) {
    StudyCalendar calendar = new StudyCalendar(LocalDate.of(2024, 2, 5));

    System.out.println("start: " + calendar.start());
    System.out.println("leap year: " + calendar.isLeapYear());
    System.out.println("modules: " + calendar.moduleStarts(3, 4));
    System.out.println("last study day: " + calendar.lastStudyDay(12));

    // дата не изменилась — LocalDate immutable
    System.out.println("start after calculations: " + calendar.start());
  }
}
