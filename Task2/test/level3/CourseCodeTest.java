package level3;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.CourseCode;

@DisplayName("CourseCode: нормализация и валидация")
class CourseCodeTest {

    @ParameterizedTest(name = "вход ''{0}'' -> ожидаем ''{1}''")
    @CsvSource({
            "JAVA-101,      JAVA-101",
            "java-101,      JAVA-101",
            "  java-101  ,  JAVA-101",
            "java_101,      JAVA-101",
            "'java 101',    JAVA-101",
            "Java-101,      JAVA-101",
            "WEB-200,       WEB-200",
            "DB-300,        DB-300"
    })
    @DisplayName("приводит разные написания к каноническому виду")
    void normalizesInput(String raw, String expected) {

        CourseCode code = CourseCode.of(raw);

        assertEquals(expected, code.value(),
                "вход '" + raw + "' должен нормализоваться в '" + expected + "'");
    }

    @ParameterizedTest(name = "''{0}'' и ''{1}'' - один и тот же курс")
    @CsvSource({
            "JAVA-101, java-101",
            "JAVA-101, '  Java_101  '",
            "WEB-200,   web 200"
    })
    @DisplayName("нормализованные коды равны между собой")
    void normalizedCodesAreEqual(String first, String second) {
        CourseCode a = CourseCode.of(first);
        CourseCode b = CourseCode.of(second);

        assertEquals(a, b, "коды '" + first + "' и '" + second + "' должны быть равны");
        assertEquals(a.hashCode(), b.hashCode(), "равные объекты должны иметь равные hashCode");
    }

    @ParameterizedTest(name = "недопустимый код ''{0}'' -> отвергнут")
    @ValueSource(strings = {
            "",
            "   ",
            "J",
            "JAV",
            "JAVASCRIPT-101",
            "JAVA101",
            "JAVA-10",
            "JAVA-1010",
            "JAVA-ABC",
            "JAVA-10A",
            "-101",
            "JAVA--101"
    })
    @DisplayName("недопустимые коды отвергаются с IllegalArgumentException")
    void rejectsInvalidCodes(String raw) {
        assertThrows(IllegalArgumentException.class, () -> CourseCode.of(raw),
                "код '" + raw + "' должен быть отвергнут");
    }

    @Test
    @DisplayName("null отвергается с IllegalArgumentException")
    void rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> CourseCode.of(null));
    }

    @ParameterizedTest(name = "''{0}'' -> isValid = {1}")
    @MethodSource("validityCases")
    @DisplayName("isValid согласован с of(): true там, где of() не бросает")
    void isValidAgreesWithFactory(String raw, boolean expected) {
        assertEquals(expected, CourseCode.isValid(raw),
                "isValid должен совпадать с успешностью CourseCode.of()");

        if (expected) {
            assertNotNull(CourseCode.of(raw), "если isValid=true, of() обязан вернуть значение");
        } else {
            assertThrows(IllegalArgumentException.class, () -> CourseCode.of(raw),
                    "если isValid=false, of() обязан бросить исключение");
        }
    }

    static Stream<Arguments> validityCases() {
        return Stream.of(

                Arguments.of("AB-101", true),
                Arguments.of("ABCD-101", true),
                Arguments.of("java-101", true),
                Arguments.of(" JAVA_101 ", true),

                Arguments.of("A-101", false),
                Arguments.of("ABCDE-101", false),

                Arguments.of("JAVA-12", false),
                Arguments.of("JAVA-1011", false),

                Arguments.of("", false),
                Arguments.of("JAVA101", false),
                Arguments.of("JAVA-ABC", false)
        );
    }

    @Test
    @DisplayName("длина буквенного префикса ограничена диапазоном 2..4")
    void prefixLengthIsBounded() {
        for (int letters = 1; letters <= 6; letters++) {
            String code = "A".repeat(letters) + "-101";
            boolean shouldBeValid = letters >= 2 && letters <= 4;
            assertEquals(shouldBeValid, CourseCode.isValid(code),
                    "префикс из " + letters + " букв: ожидалась валидность " + shouldBeValid);
        }
    }

    @Test
    @DisplayName("код курса не равен строке с тем же текстом")
    void notEqualToPlainString() {
        CourseCode code = CourseCode.of("JAVA-101");

        assertNotEquals("JAVA-101", code, "CourseCode и String - разные типы");
        assertFalse(code.equals(null), "equals(null) должен быть false, а не NPE");
        assertTrue(code.equals(CourseCode.of("java-101")), "равны нормализованные коды");
    }
}
