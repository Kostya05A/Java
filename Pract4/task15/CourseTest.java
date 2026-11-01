import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import src.Course;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Базовый тест Course: дополнительно проверим валидации
class CourseTest {
  @Test
  void validCourse() {
    Course c = new Course(1, "Java", 32);
    assertEquals(1, c.id());
    assertEquals("Java", c.title());
    assertEquals(32, c.durationHours());
  }

  @Test
  void rejectsInvalidDuration() {
    assertThrows(IllegalArgumentException.class, () -> new Course(1, "Java", 0));
    assertThrows(IllegalArgumentException.class, () -> new Course(1, "Java", 101));
  }

  @Test
  void rejectsInvalidTitle() {
    assertThrows(IllegalArgumentException.class, () -> new Course(1, "", 32));
    assertThrows(IllegalArgumentException.class, () -> new Course(1, "   ", 32));
    assertThrows(IllegalArgumentException.class, () -> new Course(1, null, 32));
  }
}