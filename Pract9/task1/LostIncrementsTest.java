package task1;

import org.junit.jupiter.api.Test;
import src.Counter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LostIncrementsTest {

    @Test
    void unsafeCounterLosesUpdates() throws Exception {
        int threads = 8, perThread = 50_000;
        Counter c = new Counter();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                ex.submit(() -> {
                    start.await();
                    for (int j = 0; j < perThread; j++) c.unsafeIncrement();
                    return null;
                });
            }
            start.countDown();
            ex.shutdown();
            assertTrue(ex.awaitTermination(30, TimeUnit.SECONDS));
        } finally {
            ex.shutdownNow();
        }
        assertTrue(c.value() < threads * perThread,
                "Ожидалась потеря инкрементов, получено " + c.value());
    }

    @Test
    void synchronizedCounterIsCorrect() throws Exception {
        int threads = 8, perThread = 50_000;
        Counter c = new Counter();
        runAll(threads, perThread, c::syncIncrement);
        assertTrue(c.value() == threads * perThread);
    }

    @Test
    void synchronizedBlockCounterIsCorrect() throws Exception {
        int threads = 8, perThread = 50_000;
        Counter c = new Counter();
        runAll(threads, perThread, c::blockIncrement);
        assertTrue(c.value() == threads * perThread);
    }

    private static void runAll(int threads, int perThread, Runnable op) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> { for (int j = 0; j < perThread; j++) op.run(); });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
    }
}
