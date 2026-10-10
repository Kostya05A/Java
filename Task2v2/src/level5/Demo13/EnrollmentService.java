package level5.Demo13;

public class EnrollmentService {
    public String enroll(String courseId, boolean courseOpen, boolean hasSeats, boolean hasPrereq, boolean isRepeat) {
        if (!courseOpen) return "COURSE_CLOSED";
        if (!hasSeats) return "NO_SEATS";
        if (!hasPrereq) return "NO_PREREQ";
        if (isRepeat) return "ALREADY_ENROLLED";
        return "SUCCESS";
    }
}