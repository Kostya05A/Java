package task6;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class Task6Demo {
    private Task6Demo() {
    }

    public static void main(String[] args) throws Exception {
        int capacity = 10;
        int threads = 100;
        List<EnrollmentService> variants = List.of(
                new SynchronizedEnrollment(capacity),
                new AtomicEnrollment(capacity),
                new ConcurrentMapEnrollment(capacity));

        System.out.println("Запись на курс: capacity=" + capacity + ", threads=" + threads);
        for (EnrollmentService service : variants) {
            int successes = run(service, threads);
            System.out.printf("%-24s successes=%d, enrolled=%d, rejected=%d%n",
                    service.getClass().getSimpleName(),
                    successes,
                    service.enrolledCount(),
                    threads - successes);
        }

        System.out.println("\nСравнение:");
        System.out.println("  synchronized: один монитор сериализует все enroll — просто, но меньше параллелизма.");
        System.out.println("  atomic: CAS на счётчике мест + concurrent set студентов — без глобальной блокировки.");
        System.out.println("  concurrent-map: Semaphore ограничивает места, ConcurrentHashMap гарантирует уникальность.");
        System.out.println("  Во всех вариантах успешных записей не больше capacity, дубликат не занимает второе место,");
        System.out.println("  а отказ (false) не оставляет изменений в состоянии.");
    }

    private static int run(EnrollmentService service, int threads) throws Exception {
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                String studentId = "student-" + i;
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
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        return successes.get();
    }
}
