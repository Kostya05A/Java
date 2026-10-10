package level4;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Course;
import domain.CourseCode;
import level1.EnrollmentRejectedException;

@DisplayName("Course: проверка отказа и состояния после него")
class CourseFailureStateTest {

    private Course course;

    @BeforeEach
    void createFreshCourse() {

        course = Course.of("JAVA-101", "Основы Java", 2);
    }

    @Test
    @DisplayName("отказ сообщает, КАКОЕ значение отвергнуто и ПОЧЕМУ")
    void failureCarriesUsefulContext() {

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> course.completeHours(100));

        assertAll(
                () -> assertTrue(ex.getMessage().contains("100"),
                        "сообщение должно называть отвергнутое значение, было: " + ex.getMessage()),
                () -> assertTrue(ex.getMessage().contains("40"),
                        "сообщение должно называть границу, было: " + ex.getMessage())
        );
    }

    @Test
    @DisplayName("у собственного исключения проверяем поля, а не текст")
    void customExceptionExposesFields() {
        long studentId = 42L;
        long courseId = 7L;
        Throwable cause = new IOException("сеть недоступна");

        EnrollmentRejectedException ex = assertThrows(
                EnrollmentRejectedException.class,
                () -> {
                    throw new EnrollmentRejectedException(
                            studentId, courseId, EnrollmentRejectedException.Reason.COURSE_CLOSED, cause);
                });

        assertAll(
                () -> assertEquals(studentId, ex.studentId()),
                () -> assertEquals(courseId, ex.courseId()),
                () -> assertEquals(EnrollmentRejectedException.Reason.COURSE_CLOSED, ex.reason()),
                () -> assertEquals(cause, ex.getCause(), "исходная причина не должна теряться")
        );
    }

    @Test
    @DisplayName("после неудачного completeHours объект не изменился")
    void stateUnchangedAfterRejectedDuration() {

        course.completeHours(20);
        int hoursBefore = course.completedHours();
        boolean completedBefore = course.isCompleted();

        assertThrows(IllegalArgumentException.class, () -> course.completeHours(100));

        assertAll(
                () -> assertEquals(hoursBefore, course.completedHours(),
                        "неудачная команда не должна менять completedHours"),
                () -> assertEquals(completedBefore, course.isCompleted(),
                        "неудачная команда не должна менять статус завершения")
        );
    }

    @Test
    @DisplayName("провал по верхней границе не затирает уже записанное значение")
    void rejectedUpdateDoesNotOverwritePreviousValue() {

        course.completeHours(30);

        assertThrows(IllegalArgumentException.class, () -> course.completeHours(41));

        assertEquals(30, course.completedHours(),
                "значение 30 должно сохраниться: отказ не должен частично применять изменения");
    }

    @Test
    @DisplayName("после отказа на enroll(closed) состояние курса не изменилось")
    void stateUnchangedAfterRejectedEnroll() {

        course.close();
        int enrolledBefore = course.enrolledCount();
        int freeBefore = course.freeSeats();

        assertThrows(IllegalStateException.class, () -> course.enroll(1L));

        assertAll(
                () -> assertEquals(enrolledBefore, course.enrolledCount(),
                        "отказ не должен никого зачислять"),
                () -> assertEquals(freeBefore, course.freeSeats(),
                        "отказ не должен расходовать места"),
                () -> assertFalse(course.isEnrolled(1L),
                        "отказавшийся студент не должен считаться зачисленным")
        );
    }

    @Test
    @DisplayName("после отказа на повторный enroll состояние не изменилось")
    void stateUnchangedAfterDuplicateEnroll() {

        course.enroll(1L);
        int enrolledBefore = course.enrolledCount();

        assertThrows(IllegalStateException.class, () -> course.enroll(1L));

        assertAll(
                () -> assertEquals(enrolledBefore, course.enrolledCount(),
                        "повторная заявка не должна увеличивать число зачисленных"),
                () -> assertEquals(1, course.freeSeats(),
                        "отказ не должен расходовать место")
        );
    }

    @Test
    @DisplayName("после отказа из-за отсутствия мест состояние не изменилось")
    void stateUnchangedWhenNoSeatsLeft() {

        course.enroll(1L);
        course.enroll(2L);
        int freeBefore = course.freeSeats();

        assertThrows(IllegalStateException.class, () -> course.enroll(3L));

        assertAll(
                () -> assertEquals(2, course.enrolledCount(), "третий не должен был зачислиться"),
                () -> assertEquals(freeBefore, course.freeSeats(), "число мест не изменилось"),
                () -> assertFalse(course.isEnrolled(3L), "третий не зачислен")
        );
    }

    @Nested
    @DisplayName("одна причина падения в lambda")
    class SingleFailingAction {

        @Test
        @DisplayName("в lambda только действие под тестом")
        void onlyActInsideLambda() {
            int invalidHours = 100;

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> course.completeHours(invalidHours));

            assertTrue(ex.getMessage().contains("100"));
        }

        @Test
        @DisplayName("несколько проверок результата - через assertAll")
        void multipleAssertionsWithAssertAll() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> course.completeHours(5));

            assertAll(
                    "причина отказа должна быть полной",
                    () -> assertTrue(ex.getMessage().contains("5")),
                    () -> assertTrue(ex.getMessage().contains("10")),
                    () -> assertTrue(ex.getMessage().contains("минимум"))
            );
        }

        @Test
        @DisplayName("подготовка, которая может бросить, вынесена до assertThrows")
        void riskySetupRunsBeforeAssertThrows() {

            String rawCode = "JAVA-101";
            CourseCode code = CourseCode.of(rawCode);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> course.completeHours(Course.MAX_DURATION_HOURS + 1));

            assertEquals("JAVA-101", code.value(), "подготовка выполнена и не помешала проверке");
            assertTrue(ex.getMessage().contains("41"));
        }
    }
}
