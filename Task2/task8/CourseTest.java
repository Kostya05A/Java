import org.junit.jupiter.api.Test;
import src.Course;
import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
  @Test
  void completeHours_increasesCompletedHours_whenEnoughRemaining() {
    // Arrange
    Course course = new Course(1, "Java", 10, 2);

    // Act
    course.completeHours(3);

    // Assert 
    assertEquals(5, course.completedHours(),
        "compoleted hours should increase by 3");
  }
}
