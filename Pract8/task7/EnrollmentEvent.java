package task7;

import src.Level;

public record EnrollmentEvent(
        String studentId,
        String courseCode,
        Level level,
        int durationHours,
        boolean completed) {
}
