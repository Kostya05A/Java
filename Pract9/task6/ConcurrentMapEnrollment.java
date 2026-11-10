package task6;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

public final class ConcurrentMapEnrollment implements EnrollmentService {
    private final int capacity;
    private final ConcurrentMap<String, Boolean> students = new ConcurrentHashMap<>();
    private final Semaphore seats;

    public ConcurrentMapEnrollment(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity должен быть >= 0");
        }
        this.capacity = capacity;
        this.seats = new Semaphore(capacity);
    }

    @Override
    public boolean enroll(String studentId) {
        if (!seats.tryAcquire()) {
            return false;
        }
        if (students.putIfAbsent(studentId, Boolean.TRUE) == null) {
            return true;
        }
        seats.release();
        return false;
    }

    @Override
    public boolean isEnrolled(String studentId) {
        return students.containsKey(studentId);
    }

    @Override
    public int enrolledCount() {
        return students.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
