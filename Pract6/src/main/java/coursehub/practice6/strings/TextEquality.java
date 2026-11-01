package coursehub.practice6.strings;

import java.util.Objects;

// правило: текст сравниваем через equals, а не через == (pool — деталь реализации)
public final class TextEquality {
  private TextEquality() {
  }

  // null-safe содержательное сравнение
  public static boolean same(String left, String right) {
    return Objects.equals(left, right);
  }
}
