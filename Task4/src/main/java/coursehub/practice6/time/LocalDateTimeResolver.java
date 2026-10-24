package coursehub.practice6.time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// политика для перехода часов: пропуск и повтор местного времени
public final class LocalDateTimeResolver {
  private LocalDateTimeResolver() {
  }

  // в «пропущенном» интервале atZone сдвигает время вперёд на величину скачка
  public static ZonedDateTime resolveGapForward(LocalDateTime local, ZoneId zone) {
    return local.atZone(zone);
  }

  // строгий режим: если после разрешения время не совпало — такого времени нет
  public static ZonedDateTime resolveStrict(LocalDateTime local, ZoneId zone) {
    ZonedDateTime resolved = local.atZone(zone);
    if (!resolved.toLocalDateTime().equals(local)) {
      throw new InvalidLocalTimeException(local, zone);
    }
    return resolved;
  }

  // политика CourseHub: несуществующее время занятия не сдвигаем молча — это ошибка расписания
  public static ZonedDateTime resolveSessionStart(LocalDateTime local, ZoneId zone) {
    return resolveStrict(local, zone);
  }

  // при «повторе» local.atZone берёт первый offset; этот метод — второй
  public static ZonedDateTime laterOffsetAtOverlap(LocalDateTime local, ZoneId zone) {
    return local.atZone(zone).withLaterOffsetAtOverlap();
  }
}
