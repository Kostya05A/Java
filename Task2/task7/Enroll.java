public class Enroll {
  public static void enroll(long studentID, long courseID) 
      throws EnrollmentRejectedException {
    if (courseID != 1) {
      throw new EnrollmentRejectedException(
          studentID, courseID,
          EnrollmentRejectedException.ReasonCode.COURSE_CLOSED);
    }
  }
}
