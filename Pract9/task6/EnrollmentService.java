package task6;

public interface EnrollmentService {
    boolean enroll(String studentId);

    boolean isEnrolled(String studentId);

    int enrolledCount();

    int capacity();
}
