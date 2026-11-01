import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

// Параметризованный тест: контракт один (нормализация), а данных много.
// Один метод вместо десяти почти одинаковых, а имя теста показывает вход и
// ожидаемый результат - по отчету видно, какой именно код сломался.
class CourseCodeTest {
  @ParameterizedTest(name = "normalize(\"{0}\") -> \"{1}\"")
  @CsvSource({
      "JAVA101,              JAVA101",
      "' java-101 ',         JAVA101",
      "'java_101',           JAVA101",
      "'Java 101',           JAVA101",
      "'  jAvA   bAsIcs ', JAVABASICS"
  })
  void normalizesRawCodeToCanonicalForm(String raw, String expected) {
    // Act
    CourseCode code = CourseCode.parse(raw);

    // Assert
    assertEquals(expected, code.value());
  }

  @ParameterizedTest(name = "parse(\"{0}\") -> IllegalArgumentException: {1}")
  @MethodSource("invalidCodes")
  void rejectsInvalidCode(String raw, String expectedMessage) {
    // Act
    IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> CourseCode.parse(raw));

    // Assert
    assertEquals(expectedMessage, e.getMessage());
  }

  static Stream<Arguments> invalidCodes() {
    return Stream.of(
        // пусто после нормализации: пробелы и разделители не делают код валидным
        Arguments.of("", "code must not be blank"),
        Arguments.of("   ", "code must not be blank"),
        Arguments.of("-_-_", "code must not be blank"),
        // короче минимума
        Arguments.of("ab", "code length must be in [3, 10]"),
        // длиннее максимума
        Arguments.of("ABCDEFGHIJK", "code length must be in [3, 10]"),
        // посторонние символы видны в сообщении, искать причину не нужно
        Arguments.of("JAVA101!", "code must contain only latin letters and digits, got '!'"));
  }

  @Test
  void acceptsBoundaryLengths() {
    // Arrange
    String shortest = "A".repeat(CourseCode.MIN_LENGTH);
    String longest = "B".repeat(CourseCode.MAX_LENGTH);

    // Act + Assert
    assertEquals(shortest, CourseCode.parse(shortest).value());
    assertEquals(longest, CourseCode.parse(longest).value());
  }

  @Test
  void parsedCodesWithSameNormalizedFormAreEqual() {
    // Arrange
    CourseCode fromCsv = CourseCode.parse("java-101");
    CourseCode fromForm = CourseCode.parse(" JAVA_101 ");

    // Act + Assert: разные источники - один и тот же код в системе
    assertEquals(fromCsv, fromForm);
    assertEquals(fromCsv.hashCode(), fromForm.hashCode());
  }

  @Test
  void parseIsIdempotent() {
    // Arrange
    CourseCode once = CourseCode.parse("java-101");

    // Act
    CourseCode twice = CourseCode.parse(once.value());

    // Assert: нормализация нормализованного не должна ломать код
    assertEquals(once, twice);
  }

  @Test
  void nullIsProgrammingErrorNotInvalidData() {
    // Act + Assert: null - баг вызывающего, его нельзя смешивать с плохими данными
    assertThrows(NullPointerException.class, () -> CourseCode.parse(null));
  }
}