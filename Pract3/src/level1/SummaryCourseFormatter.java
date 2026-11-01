package src.level1;

import src.Course;

public class SummaryCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return course.title() + " - " + course.completedHours() + "/" + course.durationHours();
    }
}
