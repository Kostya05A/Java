package task3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class SessionRegistryDemo {
    private SessionRegistryDemo() {
    }

    public static void main(String[] args) throws Exception {
        int threads = 16;
        SessionRegistry registry = new SessionRegistry();
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger winners = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                String user = "user-" + i;
                futures.add(executor.submit(() -> {
                    start.await();
                    boolean created = registry.createIfAbsent(
                            new SessionRegistry.Session("session-1", user, 0L));
                    if (created) {
                        winners.incrementAndGet();
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
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("createIfAbsent: winners=" + winners.get()
                + ", registry.size=" + registry.size());
        System.out.println("session-1=" + registry.find("session-1").orElseThrow());

        SessionRegistry.Session computed =
                registry.createIfAbsentComputed("session-2", "alice", 123L);
        System.out.println("computeIfAbsent: " + computed);

        System.out.println("remove(session-1)=" + registry.remove("session-1")
                + ", registry.size=" + registry.size());
        explain();
    }

    private static void explain() {
        System.out.println("\nПочему так:");
        System.out.println("  - ConcurrentHashMap.putIfAbsent атомарно проверяет отсутствие ключа и вставляет сессию.");
        System.out.println("  - computeIfAbsent атомарно вычисляет значение только для отсутствующего ключа.");
        System.out.println("  - Глобальная блокировка реестра не нужна: атомарность обеспечивается на уровне ключа.");
    }
}
