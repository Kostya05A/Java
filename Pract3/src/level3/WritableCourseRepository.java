package src.level3;

import src.Course;

public interface WritableCourseRepository {
    void save(Course course);
}