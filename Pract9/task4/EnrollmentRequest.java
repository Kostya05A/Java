package task4;

public record EnrollmentRequest(String id, String studentId, String courseCode) {

    public EnrollmentRequest {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id не может быть пустым");
        }
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("studentId не может быть пустым");
        }
        if (courseCode == null || courseCode.isBlank()) {
            throw new IllegalArgumentException("courseCode не может быть пустым");
        }
    }
}
