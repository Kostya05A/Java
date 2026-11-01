public class EnrollmentRejectedException extends Exception {
  public enum ReasonCode {
    COURSE_FULL,
    PREREQUISITE_MISSING,
    ALREADY_ENROLLED,
    COURSE_CLOSED
  }

  private final long studentID;
  private final long courseID;
  private final ReasonCode reasonCode;

  public EnrollmentRejectedException(long studentID, long courseID, 
      ReasonCode reasonCode) {
    super("Enrollment rejected: " + reasonCode);
    this.studentID = studentID;
    this.courseID = courseID;
    this.reasonCode = reasonCode;
  }
  
  public EnrollmentRejectedException(long studentID, long courseID,
      ReasonCode reasonCode, Throwable cause) {
    super("Enrollment rejected: " + reasonCode, cause);
    this.studentID = studentID;
    this.courseID = courseID;
    this.reasonCode = reasonCode;
  }

  public long studentID() { return studentID; }
  public long courseID() { return courseID; }
  public ReasonCode reasonCode() { return reasonCode; }

  @Override
  public String toString() {
    return "EnrollmentRejectedException{reasonCode=" + reasonCode + "}";
  }
}
