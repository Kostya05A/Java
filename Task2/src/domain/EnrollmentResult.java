package domain;

public record EnrollmentResult(
        long studentId,
        CourseCode courseCode,
        Status status,
        ErrorCode reason
) {

    public enum Status {
        ACCEPTED,
        WAITLISTED,
        REJECTED
    }

    public static EnrollmentResult accepted(long studentId, CourseCode courseCode) {
        return new EnrollmentResult(studentId, courseCode, Status.ACCEPTED, null);
    }

    public static EnrollmentResult waitlisted(long studentId, CourseCode courseCode) {
        return new EnrollmentResult(studentId, courseCode, Status.WAITLISTED, ErrorCode.NO_SEATS);
    }

    public static EnrollmentResult rejected(long studentId, CourseCode courseCode, ErrorCode reason) {
        if (reason == null) {
            throw new IllegalArgumentException("у отказа обязана быть причина");
        }
        return new EnrollmentResult(studentId, courseCode, Status.REJECTED, reason);
    }

    public boolean isAccepted() {
        return status == Status.ACCEPTED;
    }

    public boolean isRejected() {
        return status == Status.REJECTED;
    }

    @Override
    public String toString() {
        return "EnrollmentResult[" + status
                + ", student=" + studentId
                + ", course=" + courseCode
                + (reason == null ? "" : ", reason=" + reason)
                + "]";
    }
}
