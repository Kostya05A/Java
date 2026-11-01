package coursehub.practice6.strings;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// код курса: префикс и номер как отдельные части, а не одна строка
public record CourseCode(String prefix, int number) {
  // \A ... \z задают полное совпадение: "JAVA-101!" или "xJAVA-101" не пройдут
  private static final Pattern FORMAT = Pattern.compile("\\A([A-Z]+)-(\\d+)\\z");

  // парсинг с извлечением групп 1 (prefix) и 2 (number)
  public static Optional<CourseCode> parse(String raw) {
    if (raw == null) {
      return Optional.empty();
    }
    Matcher matcher = FORMAT.matcher(raw);
    if (!matcher.matches()) {
      return Optional.empty();
    }
    String prefix = matcher.group(1);
    int number = Integer.parseInt(matcher.group(2));
    return Optional.of(new CourseCode(prefix, number));
  }
}
