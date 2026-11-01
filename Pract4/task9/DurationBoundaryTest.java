import org.junit.jupiter.api.Test;
import src.Course;
import static org.junit.jupiter.api.Assertions.*;

// Почему одного "обычного" примера мало:
// пример с durationHours = 10 проходит одинаково и при контракте 1..100,
// и при 1..300, и при 1..10. Он не говорит, где граница, поэтому реализация
// может сломаться (например, 100 -> 300) вместе с тестом зеленым.
// Тест на границах фиксирует контракт: MIN - 1 отклоняется, MIN и MAX
// принимаются, MAX + 1 отклоняется.
class DurationBoundaryTest {
  @Test
  void constructor_rejectsDurationBelowMinimum() {
    // Arrange
    int tooSmall = Course.MIN_DURATION_HOURS - 1;

    // Act
    IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> new Course(1, "Java", tooSmall));

    // Assert
    assertEquals("durationHours must be in [1, 100]", e.getMessage());
  }

  @Test
  void constructor_acceptsMinimumDuration() {
    // Arrange
    int minimum = Course.MIN_DURATION_HOURS;

    // Act
    Course course = new Course(1, "Java", minimum);

    // Assert
    assertEquals(1, course.durationHours(),
        "the minimum duration itself is a valid course");
  }

  @Test
  void constructor_acceptsMaximumDuration() {
    // Arrange
    int maximum = Course.MAX_DURATION_HOURS;

    // Act
    Course course = new Course(1, "Java", maximum);

    // Assert
    assertEquals(100, course.durationHours(),
        "the maximum duration itself is a valid course");
  }

  @Test
  void constructor_rejectsDurationAboveMaximum() {
    // Arrange
    int tooBig = Course.MAX_DURATION_HOURS + 1;

    // Act
    IllegalArgumentException e = assertThrows(
        IllegalArgumentException.class,
        () -> new Course(1, "Java", tooBig));

    // Assert
    assertEquals("durationHours must be in [1, 100]", e.getMessage());
  }

  @Test
  void rejectedDuration_doesNotChangeAnyAcceptedOne() {
    // Arrange: валидный курс строится до невалидного, падение не должно его трогать
    Course course = new Course(1, "Java", Course.MAX_DURATION_HOURS);

    // Act
    assertThrows(IllegalArgumentException.class,
        () -> new Course(2, "Kotlin", Course.MAX_DURATION_HOURS + 1));

    // Assert
    assertEquals(100, course.durationHours());
    assertEquals(0, course.completedHours());
  }
}