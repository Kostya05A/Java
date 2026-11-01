package coursehub.practice6.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// момент вебинара храним как Instant и показываем в зонах явно, без systemDefault()
public class WebinarScheduleDemo {
  public static void main(String[] args) {
    ZoneId organizer = ZoneId.of("America/New_York");
    Instant webinar = ZonedDateTime.of(2024, 3, 6, 18, 0, 0, 0, organizer).toInstant();
    WebinarSchedule schedule = new WebinarSchedule(webinar);

    System.out.println("stored Instant: " + schedule.start());
    System.out.println("epoch millis:   " + schedule.start().toEpochMilli());

    System.out.println("display in three zones:");
    for (String line : schedule.displayAll(
        organizer,
        ZoneId.of("Europe/Berlin"),
        ZoneId.of("Asia/Tokyo"))) {
      System.out.println("  " + line);
    }

    // серверная зона используется только для отображения, но не для хранения момента
    ZoneId serverZone = ZoneId.systemDefault();
    ZonedDateTime onServer = schedule.displayIn(serverZone);
    System.out.println("server default zone (display only): " + onServer);
    System.out.println("same instant on server:            " + onServer.toInstant().equals(schedule.start()));
    System.out.println("business moment stays the stored Instant; server default zone does not change it");
  }
}
