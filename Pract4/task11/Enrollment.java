import src.Course;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Зачисление меняет состояние, поэтому отказ здесь опаснее, чем исключение:
// если enroll() бросит исключение ПОСЛЕ добавления в список,
// в курсе останется студент, которого никто не зачислял.
// Поэтому проверка идет до изменения состояния.
public class Enrollment {
  private final Course course;
  private final int capacity;
  private final Set<Long> students = new LinkedHashSet<>();

  public Enrollment(Course course, int capacity) {
    this.course = course;
    this.capacity = capacity;
  }

  public void enroll(long studentId) throws EnrollmentRejectedException {
    if (students.contains(studentId)) {
      throw new EnrollmentRejectedException(
          studentId, course.id(),
          EnrollmentRejectedException.ReasonCode.ALREADY_ENROLLED);
    }
    if (students.size() >= capacity) {
      throw new EnrollmentRejectedException(
          studentId, course.id(),
          EnrollmentRejectedException.ReasonCode.COURSE_FULL);
    }
    students.add(studentId);
  }

  public List<Long> enrolledStudents() {
    return List.copyOf(students);
  }

  public int seatsLeft() {
    return capacity - students.size();
  }
}