package src.level5;

import src.Course;

public interface EnrollmentRule {
    RuleResult evaluate(String studentName, Course course);
}