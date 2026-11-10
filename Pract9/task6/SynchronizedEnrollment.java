package task6;

import java.util.HashSet;
import java.util.Set;

public final class SynchronizedEnrollment implements EnrollmentService {
    private final int capacity;
    private final Set<String> students = new HashSet<>();

    public SynchronizedEnrollment(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity должен быть >= 0");
        }
        this.capacity = capacity;
    }

    @Override
    public synchronized boolean enroll(String studentId) {
        if (students.contains(studentId)) {
            return false;
        }
        if (students.size() >= capacity) {
            return false;
        }
        students.add(studentId);
        return true;
    }

    @Override
    public synchronized boolean isEnrolled(String studentId) {
        return students.contains(studentId);
    }

    @Override
    public synchronized int enrolledCount() {
        return students.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
