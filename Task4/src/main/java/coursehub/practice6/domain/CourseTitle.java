package coursehub.practice6.domain;

import java.text.Normalizer;
import java.util.Locale;

// display value и search key — разные вещи: показываем как есть, ищем по ключу
public record CourseTitle(String display, String searchKey) {
  public CourseTitle(String display) {
    this(display, searchKeyOf(display));
  }

  // pipeline нормализации: NFKC, strip, схлопывание пробелов, case folding
  public static String searchKeyOf(String raw) {
    if (raw == null) {
      throw new IllegalArgumentException("title must not be null");
    }
    return Normalizer.normalize(raw, Normalizer.Form.NFKC)
        .strip()
        .replaceAll("\\s+", " ")
        .toLowerCase(Locale.ROOT);
  }
}
