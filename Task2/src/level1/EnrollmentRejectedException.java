package level1;

public final class EnrollmentRejectedException extends RuntimeException {

    public enum Reason {
        NO_SEATS,
        PREREQ_MISSING,
        COURSE_CLOSED,
        DUPLICATE,

        COURSE_NOT_FOUND
    }

    private final long studentId;
    private final long courseId;
    private final Reason reason;

    public EnrollmentRejectedException(long studentId, long courseId, Reason reason, Throwable cause) {
        super("Enrollment rejected [reason=" + reason + "]", cause);
        this.studentId = studentId;
        this.courseId = courseId;
        this.reason = reason;
    }

    public EnrollmentRejectedException(long studentId, long courseId, Reason reason) {
        this(studentId, courseId, reason, null);
    }

    public long studentId() {
        return studentId;
    }

    public long courseId() {
        return courseId;
    }

    public Reason reason() {
        return reason;
    }

    @Override
    public String toString() {

        return "EnrollmentRejectedException[reason=" + reason
                + ", hasCause=" + (getCause() != null) + "]";
    }
}
