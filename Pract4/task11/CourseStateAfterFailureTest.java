import org.junit.jupiter.api.Test;
import src.Course;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// assertThrows отдает саму ошибку, поэтому проверяем не только класс,
// но и то, что ошибка объясняет отказ (сообщение, поля причины),
// и главное - что объект остался в прежнем состоянии.
// В lambda всегда одно падающее действие: если их два, тест не скажет,
// какое именно упало, и починить его почти невозможно.
//
//   assertThrows(IllegalStateException.class, () -> {
//     course.completeHours(999);   // падает здесь?
//     enrollment.enroll(1);       // или здесь?
//   });
class CourseStateAfterFailureTest {
  @Test
  void completeHours_rejectsMoreThanRemaining_andKeepsCourseState()
      throws EnrollmentRejectedException {
    // Arrange
    Course course = new Course(1, "Java", 10, 2);

    // Act: одно падающее действие в lambda
    IllegalStateException e = assertThrows(
        IllegalStateException.class,
        () -> course.completeHours(999));

    // Assert: причина отказа понятна из сообщения
    assertEquals("cannot complete 999 h, only 8 h left", e.getMessage());
    // Assert: объект не тронут
    assertEquals(2, course.completedHours(), "completedHours must stay 2");
    assertEquals(8, course.remainingHours(), "remainingHours must stay 8");
    assertFalse(course.isCompleted());
  }

  @Test
  void completeHours_rejectsNonPositiveHours_withoutTouchingState() {
    // Arrange
    Course course = new Course(1, "Java", 10, 5);

    // Act
    IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> course.completeHours(0));

    // Assert: hours <= 0 - это ошибка вызывающего, не отказ бизнес-правила
    assertEquals("hours must be > 0", e.getMessage());
    assertEquals(5, course.completedHours());
  }

  @Test
  void completeHours_succeeds_andThenTheSameOverflowIsRejected()
      throws EnrollmentRejectedException {
    // Arrange: сначала успешная команда, потом отказ на том же объекте
    Course course = new Course(1, "Java", 10, 0);

    // Act
    course.completeHours(4);

    // Assert
    assertEquals(4, course.completedHours());
    assertThrows(IllegalStateException.class, () -> course.completeHours(7));
    assertEquals(4, course.completedHours(),
        "a rejected command must not change hours");
    assertEquals(6, course.remainingHours());
  }

  @Test
  void enroll_rejectsDuplicate_andKeepsRoster() throws EnrollmentRejectedException {
    // Arrange
    Course course = new Course(2, "Kotlin", 20);
    Enrollment enrollment = new Enrollment(course, 2);
    enrollment.enroll(100);

    // Act: одно падающее действие в lambda
    EnrollmentRejectedException e = assertThrows(
        EnrollmentRejectedException.class,
        () -> enrollment.enroll(100));

    // Assert: сообщение и поля причины отказа
    assertEquals("Enrollment rejected: ALREADY_ENROLLED", e.getMessage());
    assertEquals(100, e.studentID());
    assertEquals(2, e.courseID());
    assertEquals(
        EnrollmentRejectedException.ReasonCode.ALREADY_ENROLLED, e.reasonCode());
    // Assert: состояние не изменилось - ни лишней записи, ни свободного места
    assertEquals(List.of(100L), enrollment.enrolledStudents());
    assertEquals(1, enrollment.seatsLeft());
  }

  @Test
  void enroll_rejectsWhenCourseIsFull_andKeepsRoster()
      throws EnrollmentRejectedException {
    // Arrange: одно место осталось, его занимает другой студент
    Course course = new Course(3, "SQL", 20);
    Enrollment enrollment = new Enrollment(course, 1);
    enrollment.enroll(100);

    // Act
    EnrollmentRejectedException e = assertThrows(
        EnrollmentRejectedException.class,
        () -> enrollment.enroll(200));

    // Assert
    assertEquals(EnrollmentRejectedException.ReasonCode.COURSE_FULL, e.reasonCode());
    assertEquals(200, e.studentID(), "the refused student must be reported");
    assertEquals(3, e.courseID());
    // Assert: отказ не занял место
    assertEquals(List.of(100L), enrollment.enrolledStudents());
    assertEquals(0, enrollment.seatsLeft());
  }

  @Test
  void rejectedStudent_isNotInRosterAndRosterKeepsWorking()
      throws EnrollmentRejectedException {
    // Arrange: единственное место занято
    Course course = new Course(4, "Go", 20);
    Enrollment enrollment = new Enrollment(course, 1);
    enrollment.enroll(100);

    // Act
    assertThrows(EnrollmentRejectedException.class, () -> enrollment.enroll(300));

    // Assert: отсутствие тоже проверяем явно, а не "должно быть пусто"
    assertFalse(enrollment.enrolledStudents().contains(300L));
    assertEquals(List.of(100L), enrollment.enrolledStudents());
    assertEquals(0, enrollment.seatsLeft());

    // Act: отказ не сломал объект - тот же отказ повторяется, список прежний
    assertThrows(EnrollmentRejectedException.class, () -> enrollment.enroll(300));

    // Assert
    assertEquals(List.of(100L), enrollment.enrolledStudents());
  }

  @Test
  void rejectedEnrollmentException_doesNotLeakDataInToString() {
    // Arrange
    Course course = new Course(5, "Rust", 20);
    Enrollment enrollment = new Enrollment(course, 0);

    // Act
    EnrollmentRejectedException e = assertThrows(
        EnrollmentRejectedException.class,
        () -> enrollment.enroll(999));

    // Assert: причина видна, персональные данные - нет
    assertEquals(
        "EnrollmentRejectedException{reasonCode=COURSE_FULL}", e.toString());
    assertFalse(e.toString().contains("999"));
  }
}