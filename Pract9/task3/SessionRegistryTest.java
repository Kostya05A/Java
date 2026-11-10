package task3;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SessionRegistryTest {

    @Test
    void createIfAbsentIsAtomic() throws Exception {
        int threads = 16;
        SessionRegistry reg = new SessionRegistry();
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger winners = new AtomicInteger();
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                ex.submit(() -> {
                    start.await();
                    if (reg.createIfAbsent(new SessionRegistry.Session("s1", "u", 0L))) {
                        winners.incrementAndGet();
                    }
                    return null;
                });
            }
            start.countDown();
            ex.shutdown();
            assertTrue(ex.awaitTermination(10, TimeUnit.SECONDS));
        } finally {
            ex.shutdownNow();
        }
        assertEquals(1, winners.get());
        assertEquals(1, reg.size());
    }
}