package src.level3;

import src.Course;

public class CapacityPolicy implements EnrollmentPolicy {
    @Override
    public boolean isEligible(String studentName, Course course) {
        System.out.println("CapacityPolicy: checking capacity for " + course.title());
        return true;
    }
}