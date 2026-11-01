import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CourseCodeTest {
  @ParameterizedTest(name = "normalize(\"{0}\") -> \"{1}\"")
  @CsvSource({
      "JAVA101,              JAVA101",
      "' java-101 ',         JAVA101",
      "'java_101',           JAVA101",
      "'Java 101',           JAVA101",
      "'  java   basics ', JAVABASICS"
  })
  void normalizesRawCodeToCanonicalForm(String raw, String expected) {
    CourseCode code = CourseCode.parse(raw);
    assertEquals(expected, code.value());
  }

  @ParameterizedTest(name = "parse(\"{0}\") -> IllegalArgumentException: {1}")
  @MethodSource("invalidCodes")
  void rejectsInvalidCode(String raw, String expectedMessage) {
    IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> CourseCode.parse(raw));
    assertEquals(expectedMessage, e.getMessage());
  }

  static Stream<Arguments> invalidCodes() {
    return Stream.of(
        Arguments.of("", "code must not be blank"),
        Arguments.of("   ", "code must not be blank"),
        Arguments.of("-_-_", "code must not be blank"),
        Arguments.of("ab", "code length must be in [3, 10]"),
        Arguments.of("ABCDEFGHIJK", "code length must be in [3, 10]"),
        Arguments.of("JAVA101!", "code must contain only latin letters and digits, got '!'"));
  }

  @Test
  void acceptsBoundaryLengths() {
    assertEquals("AAA", CourseCode.parse("AAA").value());
    String longest = "B".repeat(10);
    assertEquals(longest, CourseCode.parse(longest).value());
  }
}