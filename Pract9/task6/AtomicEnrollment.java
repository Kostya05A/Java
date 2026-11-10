package task6;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicEnrollment implements EnrollmentService {
    private final int capacity;
    private final Set<String> students = ConcurrentHashMap.newKeySet();
    private final AtomicInteger seats = new AtomicInteger();

    public AtomicEnrollment(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity должен быть >= 0");
        }
        this.capacity = capacity;
    }

    @Override
    public boolean enroll(String studentId) {
        if (!students.add(studentId)) {
            return false;
        }
        while (true) {
            int used = seats.get();
            if (used >= capacity) {
                students.remove(studentId);
                return false;
            }
            if (seats.compareAndSet(used, used + 1)) {
                return true;
            }
        }
    }

    @Override
    public boolean isEnrolled(String studentId) {
        return students.contains(studentId);
    }

    @Override
    public int enrolledCount() {
        return seats.get();
    }

    @Override
    public int capacity() {
        return capacity;
    }
}
