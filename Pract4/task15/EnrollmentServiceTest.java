import org.junit.jupiter.api.Test;
import src.Course;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceTest {
  @Test
  void enrollSuccess() throws EnrollmentRejectedException {
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.OPEN);
    course.setCapacity(5);
    course.setHasPrerequisite(true);
    EnrollmentService s = new EnrollmentService();
    s.enroll(course, 42);
    assertEquals(1, course.enrolledCount());
  }

  @Test
  void rejectClosed() {
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.CLOSED);
    course.setCapacity(5);
    course.setHasPrerequisite(true);
    EnrollmentService s = new EnrollmentService();
    EnrollmentRejectedException e = assertThrows(EnrollmentRejectedException.class,
        () -> s.enroll(course, 42));
    assertEquals(EnrollmentRejectedException.ReasonCode.COURSE_CLOSED, e.reasonCode());
  }

  @Test
  void rejectFull() {
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.OPEN);
    course.setCapacity(0);
    course.setHasPrerequisite(true);
    EnrollmentService s = new EnrollmentService();
    EnrollmentRejectedException e = assertThrows(EnrollmentRejectedException.class,
        () -> s.enroll(course, 42));
    assertEquals(EnrollmentRejectedException.ReasonCode.COURSE_FULL, e.reasonCode());
  }

  @Test
  void rejectPrereq() {
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.OPEN);
    course.setCapacity(5);
    course.setHasPrerequisite(false);
    EnrollmentService s = new EnrollmentService();
    EnrollmentRejectedException e = assertThrows(EnrollmentRejectedException.class,
        () -> s.enroll(course, 42));
    assertEquals(EnrollmentRejectedException.ReasonCode.PREREQUISITE_MISSING, e.reasonCode());
  }

  @Test
  void rejectAlready() throws EnrollmentRejectedException {
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.OPEN);
    course.setCapacity(5);
    course.setHasPrerequisite(true);
    course.addEnrolled(42);
    EnrollmentService s = new EnrollmentService();
    EnrollmentRejectedException e = assertThrows(EnrollmentRejectedException.class,
        () -> s.enroll(course, 42));
    assertEquals(EnrollmentRejectedException.ReasonCode.ALREADY_ENROLLED, e.reasonCode());
  }

  @Test
  void rejectDoesNotChangeState() {
    Course course = new Course(1, "Java", 32);
    course.setStatus(Course.Status.CLOSED);
    course.setCapacity(5);
    course.setHasPrerequisite(true);
    int before = course.enrolledCount();
    EnrollmentService s = new EnrollmentService();
    assertThrows(EnrollmentRejectedException.class, () -> s.enroll(course, 42));
    assertEquals(before, course.enrolledCount());
  }
}