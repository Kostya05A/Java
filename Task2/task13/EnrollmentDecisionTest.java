import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import src.Course;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

// Таблица решений для зачисления (4 условия):
// 1) статус курса: OPEN / CLOSED
// 2) наличие мест: есть / нет (enrolledCount < capacity)
// 3) prerequisite выполнен: true / false
// 4) повторная заявка: false / true
//
// При фиксированном порядке проверок мы не создаём дублирующих тестов,
// а именно проверяем РЕАЛЬНОЕ правило зачисления.
class EnrollmentDecisionTest {
  @ParameterizedTest(name = "{index}: status={0}, hasSeats={1}, hasPrereq={2}, repeat={3} -> {4}")
  @MethodSource("cases")
  void enrollmentDecision(
      Course.Status status,
      boolean hasSeats,
      boolean hasPrereq,
      boolean repeat,
      Outcome expected) {
    // Arrange
    Course course = new Course(1, "Java", 32);
    course.setStatus(status);
    if (hasSeats) {
      course.setCapacity(2);
    } else {
      course.setCapacity(0);
    }
    course.setHasPrerequisite(hasPrereq);
    if (repeat) {
      course.addEnrolled(42);
    }
    EnrollmentService service = new EnrollmentService();
    long target = repeat ? 43 : 42;
    // Act + Assert
    if (expected == Outcome.ENROLL_OK) {
      int before = course.enrolledCount();
      assertDoesNotThrow(() -> service.enroll(course, target));
      assertTrue(course.enrolledStudents().contains(target));
      assertEquals(before + 1, course.enrolledCount());
    } else {
      int before = course.enrolledCount();
      EnrollmentRejectedException e = assertThrows(
          EnrollmentRejectedException.class,
          () -> service.enroll(course, 42));
      assertEquals(expected.reason(), e.reasonCode());
      assertEquals(before, course.enrolledStudents().size(),
          "rejected enrollment must not add student");
    }
  }

  static Stream<Arguments> cases() {
    return Stream.of(
        // OPEN, есть места, prereq есть, не повтор - успех
        Arguments.of(Course.Status.OPEN, true, true, false, Outcome.ENROLL_OK),
        Arguments.of(Course.Status.CLOSED, true, true, false, Outcome.REJECT_CLOSED),
        Arguments.of(Course.Status.OPEN, false, true, false, Outcome.REJECT_FULL),
        Arguments.of(Course.Status.OPEN, true, false, false, Outcome.REJECT_PREREQ),
        Arguments.of(Course.Status.OPEN, true, true, true, Outcome.REJECT_ALREADY)
    );
  }

  // Проверка эквивалентности: при CLOSED любые комбинации ведут к COURSE_CLOSED,
  // при FULL (нет мест) - к COURSE_FULL и т.п. Таблица не растёт на все 16,
  // потому что границы покрыты минимально необходимыми значимыми случаями.
  @Test
  void whenClosed_noMatterOtherFactors_rejectedAsClosed() {
    // CLOSED + нет мест + нет prereq + повторная заявка
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.CLOSED);
    course.setCapacity(0);
    course.setHasPrerequisite(false);
    course.addEnrolled(5);
    course.addEnrolled(42);

    EnrollmentService service = new EnrollmentService();
    EnrollmentRejectedException e = assertThrows(
        EnrollmentRejectedException.class,
        () -> service.enroll(course, 42));

    assertEquals(EnrollmentRejectedException.ReasonCode.COURSE_CLOSED, e.reasonCode());
    // состояние осталось прежним
    assertEquals(2, course.enrolledCount());
  }

  @Test
  void whenOpenAndFull_rejectedAsFull_evenIfPrereqMissingOrRepeatWouldMatterLater() {
    // есть приоритет у FULL перед PREREQ/ALREADY согласно порядку
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.OPEN);
    course.setCapacity(0);
    course.setHasPrerequisite(false);
    EnrollmentService service = new EnrollmentService();

    EnrollmentRejectedException e = assertThrows(
        EnrollmentRejectedException.class,
        () -> service.enroll(course, 42));
    assertEquals(EnrollmentRejectedException.ReasonCode.COURSE_FULL, e.reasonCode());
  }

  @Test
  void whenOpenAndHasSeatsAndHasPrereqAndAlreadyEnrolled_rejectedAsAlready() {
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.OPEN);
    course.setCapacity(5);
    course.setHasPrerequisite(true);
    course.addEnrolled(42);
    EnrollmentService service = new EnrollmentService();

    EnrollmentRejectedException e = assertThrows(
        EnrollmentRejectedException.class,
        () -> service.enroll(course, 42));
    assertEquals(EnrollmentRejectedException.ReasonCode.ALREADY_ENROLLED, e.reasonCode());
  }

  enum Outcome {
    ENROLL_OK(null),
    REJECT_CLOSED(EnrollmentRejectedException.ReasonCode.COURSE_CLOSED),
    REJECT_FULL(EnrollmentRejectedException.ReasonCode.COURSE_FULL),
    REJECT_PREREQ(EnrollmentRejectedException.ReasonCode.PREREQUISITE_MISSING),
    REJECT_ALREADY(EnrollmentRejectedException.ReasonCode.ALREADY_ENROLLED);

    private final EnrollmentRejectedException.ReasonCode reason;

    Outcome(EnrollmentRejectedException.ReasonCode reason) {
      this.reason = reason;
    }

    EnrollmentRejectedException.ReasonCode reason() {
      return reason;
    }
  }
}