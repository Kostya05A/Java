package task5;

import src.CourseEntry;

public record CourseView(String code, String title, int durationHours) {
    public static CourseView of(CourseEntry course) {
        return new CourseView(course.code(), course.title(), course.durationHours());
    }
}
