import org.junit.jupiter.api.Test;

import level3.Demo8.Course;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {
    @Test
    void completeHoursAddsToTotal() {
        Course course = new Course(10);
        
        course.completeHours(4);
        
        assertEquals(4, course.getCompletedHours());
        assertFalse(course.isFinished());
    }
}