package level3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Course;

@DisplayName("Границы продолжительности курса")
class CourseDurationBoundariesTest {

    @ParameterizedTest(name = "часов = {0} - допустимая граница")
    @ValueSource(ints = {Course.MIN_DURATION_HOURS, Course.MAX_DURATION_HOURS})
    @DisplayName("допустимые границы принимаются и сохраняются")
    void acceptsBoundaryValues(int hours) {

        Course course = Course.of("JAVA-101", "Основы Java", 30);

        course.completeHours(hours);

        assertEquals(hours, course.completedHours(),
                "граничное значение " + hours + " должно сохраняться без изменений");
    }

    @ParameterizedTest(name = "часов = {0} - {1}")
    @CsvSource({
            "0,   меньше минимума",
            "9,   меньше минимума",
            "-5,  меньше минимума",
            "41,  больше максимума",
            "100, больше максимума"
    })
    @DisplayName("значения вне диапазона отвергаются с IllegalArgumentException")
    void rejectsValuesOutOfRange(int hours, String expectedHint) {

        Course course = Course.of("JAVA-101", "Основы Java", 30);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> course.completeHours(hours));

        assertTrue(ex.getMessage().contains(expectedHint),
                "сообщение должно объяснять проблему, ожидалось упоминание: " + expectedHint);
    }

    @Test
    @DisplayName("значение на единицу ниже минимума отвергается")
    void rejectsOneBelowMin() {
        Course course = Course.of("JAVA-101", "Основы Java", 30);
        assertThrows(IllegalArgumentException.class, () -> course.completeHours(Course.MIN_DURATION_HOURS - 1));
    }

    @Test
    @DisplayName("значение на единицу выше максимума отвергается")
    void rejectsOneAboveMax() {
        Course course = Course.of("JAVA-101", "Основы Java", 30);
        assertThrows(IllegalArgumentException.class, () -> course.completeHours(Course.MAX_DURATION_HOURS + 1));
    }
}
