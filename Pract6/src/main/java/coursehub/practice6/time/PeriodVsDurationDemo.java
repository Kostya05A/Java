package coursehub.practice6.time;

import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// 10 марта 2024 в США перевели часы вперёд: календарный день короче 24 часов
public class PeriodVsDurationDemo {
  public static void main(String[] args) {
    ZoneId zone = ZoneId.of("America/New_York");
    Instant start = ZonedDateTime.of(2024, 3, 10, 0, 30, 0, 0, zone).toInstant();
    WebinarSchedule schedule = new WebinarSchedule(start);

    ZonedDateTime calendarDay = schedule.plusCalendarDays(1, zone);
    Instant exact24h = schedule.plusExactHours(24);

    System.out.println("start:            " + start.atZone(zone));
    System.out.println("+1 календарный:   " + calendarDay);
    System.out.println("+24 часа:         " + exact24h.atZone(zone));

    // в этот день календарный день равен 23 часам, а не 24
    long hours = Duration.between(start, calendarDay.toInstant()).toHours();
    System.out.println("часов в календарном дне: " + hours);
    System.out.println("моменты совпали: " + calendarDay.toInstant().equals(exact24h));

    // срок подписки считаем календарными месяцами: местное время остаётся тем же
    ZonedDateTime subscriptionStart = ZonedDateTime.of(2024, 2, 10, 12, 0, 0, 0, zone);
    ZonedDateTime nextBilling = subscriptionStart.plus(Period.ofMonths(1));
    System.out.println("подписка, старт:              " + subscriptionStart);
    System.out.println("подписка, +1 месяц (Period):  " + nextBilling);

    // timeout считаем точным прошедшим временем, зона на него не влияет
    Instant timeoutEnd = subscriptionStart.toInstant().plus(Duration.ofMinutes(30));
    System.out.println("timeout, +30 минут (Duration): " + timeoutEnd.atZone(zone));

    System.out.println("срок подписки -> Period (календарь и локальное время)");
    System.out.println("timeout       -> Duration (точный отрезок времени)");
  }
}
