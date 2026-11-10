package task1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import src.Counter;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LostIncrementsTest {

    @Test
    @Timeout(60)
    void unsafeCounterLosesUpdates() throws Exception {
        int threads = 8;
        int perThread = 50_000;
        int rounds = 3;
        int lostRounds = 0;
        int lastValue = 0;

        for (int round = 0; round < rounds; round++) {
            Counter counter = new Counter();
            runAll(threads, perThread, counter::unsafeIncrement);
            lastValue = counter.value();
            if (lastValue < threads * perThread) {
                lostRounds++;
            }
        }

        assertTrue(lostRounds > 0,
                "Ожидалась потеря инкрементов хотя бы в одном раунде, последнее значение " + lastValue);
    }

    @Test
    @Timeout(30)
    void synchronizedCounterIsCorrect() throws Exception {
        int threads = 8;
        int perThread = 50_000;
        Counter counter = new Counter();
        runAll(threads, perThread, counter::syncIncrement);
        assertEquals(threads * perThread, counter.value());
    }

    @Test
    @Timeout(30)
    void synchronizedBlockCounterIsCorrect() throws Exception {
        int threads = 8;
        int perThread = 50_000;
        Counter counter = new Counter();
        runAll(threads, perThread, counter::blockIncrement);
        assertEquals(threads * perThread, counter.value());
    }

    private static void runAll(int threads, int perThread, Runnable operation) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int j = 0; j < perThread; j++) {
                    operation.run();
                }
            }, "increment-worker-" + i);
        }
        for (Thread worker : workers) {
            worker.start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
    }
}
