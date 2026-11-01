package coursehub.practice6.time;

import java.time.Instant;
import java.time.ZoneId;
import java.util.TimeZone;

// один момент — разные местные времена; серверная зона не меняет бизнес-результат
public class ZoneDisplayDemo {
  public static void main(String[] args) {
    Instant webinar = Instant.parse("2024-06-01T15:00:00Z");
    WebinarSchedule schedule = new WebinarSchedule(webinar);

    for (String line : schedule.displayAll(
        ZoneId.of("UTC"),
        ZoneId.of("America/New_York"),
        ZoneId.of("Asia/Novosibirsk"))) {
      System.out.println(line);
    }

    // смена default zone сервера не трогает сохранённый Instant
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
    System.out.println("Instant unchanged: " + schedule.start().equals(webinar));
    System.out.println("again in UTC:     " + schedule.displayIn(ZoneId.of("UTC")).toLocalDateTime());
  }
}