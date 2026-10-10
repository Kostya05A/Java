package src.level5;

import src.Course;

public class CapacityRule implements EnrollmentRule {
    @Override
    public RuleResult evaluate(String studentName, Course course) {
        System.out.println("CapacityRule: evaluating " + course.title());
        return new Pass();
    }
}