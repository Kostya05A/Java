package src.level3;

import src.Course;

public class AgePolicy implements EnrollmentPolicy {
    private final int minAge;

    public AgePolicy(int minAge) {
        this.minAge = minAge;
    }

    @Override
    public boolean isEligible(String studentName, Course course) {
        System.out.println("AgePolicy: checking age for " + studentName);
        return true;
    }
}