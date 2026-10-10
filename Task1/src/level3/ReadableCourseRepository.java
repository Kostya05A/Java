package src.level3;

import src.Course;

public interface ReadableCourseRepository {
    Course findById(long id);
}