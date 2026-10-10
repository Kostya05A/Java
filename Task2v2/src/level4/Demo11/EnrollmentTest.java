import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentTest {
    @Test
    void confirmFailsForBlockedStudentAndStateUnchanged() {
        Enrollment enrollment = new Enrollment("BLOCKED_123", "C1");
        
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            enrollment.confirm();
        });
        
        assertEquals("Student is blocked", ex.getMessage());
        assertFalse(enrollment.isConfirmed());
    }
}