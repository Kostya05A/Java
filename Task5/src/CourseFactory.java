package src;

public final class CourseFactory {
    private CourseFactory() {
    }

    public static Course create(long id, String title, int durationHours, int completedHours) {
        return new Course(id, title, durationHours, completedHours);
    }

    public static Course create(long id, String title, int durationHours) {
        return new Course(id, title, durationHours);
    }
}
