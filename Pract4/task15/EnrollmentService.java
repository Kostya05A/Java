import src.Course;

import java.util.Set;

public class EnrollmentService {
  public void enroll(Course course, long studentId) throws EnrollmentRejectedException {
    // порядок проверок фиксируем: сначала статус,
    // затем места, затем prerequisite, затем повторная заявка
    if (course.status() != Course.Status.OPEN) {
      throw new EnrollmentRejectedException(
          studentId, course.id(),
          EnrollmentRejectedException.ReasonCode.COURSE_CLOSED);
    }
    if (course.enrolledCount() >= course.capacity()) {
      throw new EnrollmentRejectedException(
          studentId, course.id(),
          EnrollmentRejectedException.ReasonCode.COURSE_FULL);
    }
    if (!course.hasPrerequisite()) {
      throw new EnrollmentRejectedException(
          studentId, course.id(),
          EnrollmentRejectedException.ReasonCode.PREREQUISITE_MISSING);
    }
    // повторная заявка
    if (course.enrolledStudents().contains(studentId)) {
      throw new EnrollmentRejectedException(
          studentId, course.id(),
          EnrollmentRejectedException.ReasonCode.ALREADY_ENROLLED);
    }
    course.addEnrolled(studentId);
  }
}