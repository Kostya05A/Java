package src.level3;

import src.Course;

public interface EnrollmentPolicy {
    boolean isEligible(String studentName, Course course);
}