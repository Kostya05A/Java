package src;

public record CourseEntry(
        String code,
        String title,
        Level level,
        int durationHours,
        int completedHours) {

    public CourseEntry {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code не может быть пустым");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title не может быть пустым");
        }
        if (level == null) {
            throw new IllegalArgumentException("level не может быть null");
        }
        if (durationHours < 1 || durationHours > 300) {
            throw new IllegalArgumentException("durationHours вне диапазона 1..300");
        }
        if (completedHours < 0 || completedHours > durationHours) {
            throw new IllegalArgumentException("completedHours вне диапазона");
        }
    }

    public CourseEntry(String code, String title, Level level, int durationHours) {
        this(code, title, level, durationHours, 0);
    }
}
