import java.util.Optional;
import src.Course;

public class FindCourse {
  public static Optional<Course> find(long id) {
    if (id == 1) {
      return Optional.of(new Course(1, "Java", 10));
    }
    return Optional.empty();
  }
}
