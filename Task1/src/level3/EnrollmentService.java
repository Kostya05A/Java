package src.level3;

import src.Course;

public class EnrollmentService {
    private final EnrollmentPolicy policy;

    public EnrollmentService(EnrollmentPolicy policy) {
        this.policy = policy;
    }

    public void enroll(String studentName, Course course) {
        if (policy.isEligible(studentName, course)) {
            System.out.println("Student " + studentName + " enrolled in " + course.title());
        } else {
            System.out.println("Student " + studentName + " cannot enroll in " + course.title());
        }
    }
}