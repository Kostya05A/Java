package coursehub.practice6.time;

import java.time.LocalDateTime;
import java.time.ZoneId;

// местное время, которого в зоне не существует (пропуск при переходе на лето)
public class InvalidLocalTimeException extends RuntimeException {
  private final LocalDateTime localTime;
  private final ZoneId zone;

  public InvalidLocalTimeException(LocalDateTime localTime, ZoneId zone) {
    super("local time " + localTime + " does not exist in zone " + zone);
    this.localTime = localTime;
    this.zone = zone;
  }

  public LocalDateTime localTime() {
    return localTime;
  }

  public ZoneId zone() {
    return zone;
  }
}
