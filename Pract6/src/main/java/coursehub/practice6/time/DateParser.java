package coursehub.practice6.time;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

// dd.MM.uuuu: строгий разбор — невозможные даты и лишний текст отклоняются
public final class DateParser {
  // ResolverStyle.STRICT не даёт «умно» превратить 31.02 в 29.02
  private static final DateTimeFormatter FORMAT =
      DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);

  private DateParser() {
  }

  public static LocalDate parse(String raw) {
    try {
      return LocalDate.parse(raw, FORMAT);
    } catch (DateTimeParseException e) {
      throw new InvalidDateFieldException("date", raw, e);
    }
  }
}
