package src.level1;

import src.Course;

public class DetailedCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return String.format("Курс: %s\nID: %d\nДлительность: %d ч.\nПройдено: %d ч.\nСтатус: %s",
                course.title(), course.id(), course.durationHours(), 
                course.completedHours(), course.isCompleted() ? "Завершен" : "В процессе");
    }
}
