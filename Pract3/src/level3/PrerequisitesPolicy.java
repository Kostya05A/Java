package src.level3;

import src.Course;

public class PrerequisitesPolicy implements EnrollmentPolicy {
    @Override
    public boolean isEligible(String studentName, Course course) {
        System.out.println("PrerequisitesPolicy: checking prerequisites for " + studentName);
        return true;
    }
}