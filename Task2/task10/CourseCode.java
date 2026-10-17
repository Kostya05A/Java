import java.util.Locale;

// Код курса приходит из разных источников ("java-101", " JAVA_101 ", "Java 101"),
// но в системе он должен быть один. Нормализация убирает пробелы и разделители,
// приводит к верхнему регистру и только потом проверяется:
// порядок важен, иначе "   " превратится в валидный код.
public class CourseCode {
  public static final int MIN_LENGTH = 3;
  public static final int MAX_LENGTH = 10;

  private final String value;

  private CourseCode(String value) {
    this.value = value;
  }

  public static CourseCode parse(String raw) {
    // null - это ошибка программиста, а не плохие данные:
    // для данных есть IllegalArgumentException с причиной
    String normalized = normalize(raw);

    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("code must not be blank");
    }
    if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
      throw new IllegalArgumentException(
          "code length must be in [" + MIN_LENGTH + ", " + MAX_LENGTH + "]");
    }
    for (int i = 0; i < normalized.length(); i++) {
      char c = normalized.charAt(i);
      boolean latinLetterOrDigit =
          (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
      if (!latinLetterOrDigit) {
        throw new IllegalArgumentException(
            "code must contain only latin letters and digits, got '" + c + "'");
      }
    }
    return new CourseCode(normalized);
  }

  public static String normalize(String raw) {
    return raw.trim()
        .toUpperCase(Locale.ROOT)
        .replace(" ", "")
        .replace("-", "")
        .replace("_", "");
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CourseCode other)) return false;
    return value.equals(other.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}