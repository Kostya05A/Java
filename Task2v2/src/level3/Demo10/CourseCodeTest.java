import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class CourseCodeTest {
    @ParameterizedTest(name = "Код {0} нормализуется в {1}")
    @CsvSource({
        "'cs 101', 'CS-101'",
        "'math202', 'MATH202'",
        "'  phy 303  ', 'PHY-303'"
    })
    void normalizesValidCodes(String input, String expected) {
        CourseCode code = new CourseCode(input);
        assertEquals(expected, code.getNormalizedCode());
    }

    @ParameterizedTest(name = "Код {0} вызывает исключение")
    @CsvSource({
        "''",
        "'   '",
        "'invalid@code'"
    })
    void rejectsInvalidCodes(String input) {
        assertThrows(IllegalArgumentException.class, () -> new CourseCode(input));
    }
}