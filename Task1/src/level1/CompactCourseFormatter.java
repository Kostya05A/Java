package src.level1;

import src.Course;

public class CompactCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return String.format("[%d] %s (%d ч. осталось)", 
                course.id(), course.title(), course.remainingHours());
    }
}
