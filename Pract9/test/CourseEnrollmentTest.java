package task6;

import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseEnrollmentTest {
    private static final int CAPACITY = 8;

    static Stream<Arguments> variants() {
        return Stream.of(
                Arguments.of("synchronized", new SynchronizedEnrollment(CAPACITY)),
                Arguments.of("atomic", new AtomicEnrollment(CAPACITY)),
                Arguments.of("concurrent-map", new ConcurrentMapEnrollment(CAPACITY)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("variants")
    @Timeout(10)
    void concurrentEnrollNeverExceedsCapacity(String name, EnrollmentService service) throws Exception {
        int threads = 64;
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        Set<String> successfulStudents = ConcurrentHashMap.newKeySet();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                String studentId = "student-" + i;
                futures.add(executor.submit(() -> {
                    start.await();
                    if (service.enroll(studentId)) {
                        successes.incrementAndGet();
                        successfulStudents.add(studentId);
                    }
                    return null;
                }));
            }

            start.countDown();
            for (Future<?> future : futures) {
                future.get(5, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }

        assertEquals(CAPACITY, successes.get());
        assertEquals(CAPACITY, service.enrolledCount());
        assertEquals(CAPACITY, successfulStudents.size());

        for (int i = 0; i < threads; i++) {
            String studentId = "student-" + i;
            assertEquals(successfulStudents.contains(studentId), service.isEnrolled(studentId));
        }

        int beforeRejection = service.enrolledCount();
        assertFalse(service.enroll("late-student"));
        assertEquals(beforeRejection, service.enrolledCount());
        assertFalse(service.isEnrolled("late-student"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("variants")
    @Timeout(10)
    void sameStudentCannotOccupyTwoSeats(String name, EnrollmentService service) throws Exception {
        int threads = 32;
        String studentId = "duplicate-student";
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    if (service.enroll(studentId)) {
                        successes.incrementAndGet();
                    }
                    return null;
                }));
            }

            start.countDown();
            for (Future<?> future : futures) {
                future.get(5, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }

        assertEquals(1, successes.get());
        assertEquals(1, service.enrolledCount());
        assertTrue(service.isEnrolled(studentId));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("variants")
    void rejectedEnrollmentDoesNotChangeState(String name, EnrollmentService service) {
        for (int i = 0; i < CAPACITY; i++) {
            assertTrue(service.enroll("student-" + i));
        }

        int beforeRejection = service.enrolledCount();
        assertFalse(service.enroll("extra-student"));
        assertEquals(beforeRejection, service.enrolledCount());
        assertFalse(service.isEnrolled("extra-student"));
        assertEquals(CAPACITY, service.enrolledCount());
    }
}
