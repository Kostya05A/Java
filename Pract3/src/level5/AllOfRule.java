package src.level5;

import src.Course;
import java.util.List;

public class AllOfRule implements EnrollmentRule {
    private final List<EnrollmentRule> rules;

    public AllOfRule(List<EnrollmentRule> rules) {
        this.rules = rules;
    }

    @Override
    public RuleResult evaluate(String studentName, Course course) {
        for (EnrollmentRule rule : rules) {
            RuleResult result = rule.evaluate(studentName, course);
            if (result instanceof Fail f) {
                return f;
            }
        }
        return new Pass();
    }
}