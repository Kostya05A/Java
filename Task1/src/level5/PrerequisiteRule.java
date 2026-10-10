package src.level5;

import src.Course;

public class PrerequisiteRule implements EnrollmentRule {
    @Override
    public RuleResult evaluate(String studentName, Course course) {
        System.out.println("PrerequisiteRule: evaluating " + studentName);
        return new Pass();
    }
}