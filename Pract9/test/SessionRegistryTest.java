package task3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionRegistryTest {

    @Test
    @Timeout(10)
    void createIfAbsentIsAtomic() throws Exception {
        int threads = 16;
        SessionRegistry registry = new SessionRegistry();
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger winners = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    if (registry.createIfAbsent(new SessionRegistry.Session("s1", "u", 0L))) {
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
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
        assertEquals(1, winners.get());
        assertEquals(1, registry.size());
    }

    @Test
    @Timeout(10)
    void computeIfAbsentReturnsSingleSessionForAllThreads() throws Exception {
        int threads = 16;
        SessionRegistry registry = new SessionRegistry();
        CountDownLatch start = new CountDownLatch(1);
        Set<SessionRegistry.Session> sessions = ConcurrentHashMap.newKeySet();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                String user = "user-" + i;
                futures.add(executor.submit(() -> {
                    start.await();
                    sessions.add(registry.createIfAbsentComputed("s2", user, 42L));
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
        assertEquals(1, sessions.size());
        assertEquals(1, registry.size());
    }
}
