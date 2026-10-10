import org.junit.jupiter.api.Test;

import level3.Demo9.CourseDuration;

import static org.junit.jupiter.api.Assertions.*;

class CourseDurationTest {
    @Test
    void belowMinimumThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CourseDuration(9));
    }

    @Test
    void exactMinimumIsValid() {
        CourseDuration course = new CourseDuration(10);
        assertEquals(10, course.getHours());
    }

    @Test
    void exactMaximumIsValid() {
        CourseDuration course = new CourseDuration(100);
        assertEquals(100, course.getHours());
    }

    @Test
    void aboveMaximumThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new CourseDuration(101));
    }
}