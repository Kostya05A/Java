package src.level5;

import src.Course;
import java.util.List;

public class AnyOfRule implements EnrollmentRule {
    private final List<EnrollmentRule> rules;

    public AnyOfRule(List<EnrollmentRule> rules) {
        this.rules = rules;
    }

    @Override
    public RuleResult evaluate(String studentName, Course course) {
        for (EnrollmentRule rule : rules) {
            RuleResult result = rule.evaluate(studentName, course);
            if (result instanceof Pass) {
                return result;
            }
        }
        return new Fail("No rules passed");
    }
}