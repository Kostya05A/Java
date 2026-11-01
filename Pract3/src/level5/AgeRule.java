package src.level5;

import src.Course;

public class AgeRule implements EnrollmentRule {
    @Override
    public RuleResult evaluate(String studentName, Course course) {
        System.out.println("AgeRule: evaluating " + studentName);
        return new Pass();
    }
}