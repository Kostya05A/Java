package level3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Course;

@DisplayName("Course.completeHours")
class CourseCompletionTest {

    @Test
    @DisplayName("успешный вызов сохраняет переданное количество часов")
    void completeHoursStoresGivenValue() {

        Course course = Course.of("JAVA-101", "Основы Java", 30);

        course.completeHours(24);

        assertEquals(24, course.completedHours(),
                "после completeHours(24) должно сохраниться ровно 24");
    }

    @Test
    @DisplayName("после completeHours курс считается завершённым")
    void completeHoursMarksCourseAsCompleted() {

        Course course = Course.of("JAVA-101", "Основы Java", 30);

        course.completeHours(10);

        assertTrue(course.isCompleted(), "после completeHours(10) isCompleted() должен быть true");
    }
}
