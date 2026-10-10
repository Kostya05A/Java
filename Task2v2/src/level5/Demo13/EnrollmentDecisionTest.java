import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentDecisionTest {
    private final EnrollmentService service = new EnrollmentService();

    @Test
    void successWhenAllConditionsMet() {
        assertEquals("SUCCESS", service.enroll("C1", true, true, true, false));
    }

    @Test
    void failIfCourseClosed() {
        assertEquals("COURSE_CLOSED", service.enroll("C1", false, true, true, false));
    }

    @Test
    void failIfNoSeats() {
        assertEquals("NO_SEATS", service.enroll("C1", true, false, true, false));
    }

    @Test
    void failIfNoPrerequisite() {
        assertEquals("NO_PREREQ", service.enroll("C1", true, true, false, false));
    }

    @Test
    void failIfRepeatApplication() {
        assertEquals("ALREADY_ENROLLED", service.enroll("C1", true, true, true, true));
    }
}
