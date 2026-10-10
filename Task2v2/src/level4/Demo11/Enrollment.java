package level4.Demo11;

public class Enrollment {
    private final String studentId;
    private final String courseId;
    private boolean confirmed;

    public Enrollment(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.confirmed = false;
    }

    public void confirm() {
        if (studentId.startsWith("BLOCKED")) {
            throw new IllegalStateException("Student is blocked");
        }
        this.confirmed = true;
    }

    public boolean isConfirmed() { return confirmed; }
    public String getStudentId() { return studentId; }
}
