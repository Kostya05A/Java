package task5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeterministicRaceTest {

    @Test
    @Timeout(5)
    void controlledProtocolInterleavingLosesOneUpdate() throws Exception {
        ProtocolCounter counter = new ProtocolCounter();
        Phaser protocol = new Phaser(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(() -> {
                start.await();
                counter.unsafeIncrementWithProtocol(protocol);
                return null;
            });
            Future<?> second = executor.submit(() -> {
                start.await();
                counter.unsafeIncrementWithProtocol(protocol);
                return null;
            });

            start.countDown();
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }

        assertEquals(1, counter.value());
    }

    @Test
    @Timeout(10)
    void synchronizedIncrementIsCorrectUnderControlledStart() throws Exception {
        int threads = 8;
        int perThread = 20_000;
        ProtocolCounter counter = new ProtocolCounter();
        Phaser start = new Phaser(threads + 1);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            for (int i = 0; i < threads; i++) {
                executor.submit(() -> {
                    start.arriveAndAwaitAdvance();
                    for (int j = 0; j < perThread; j++) {
                        counter.syncIncrement();
                    }
                    return null;
                });
            }

            start.arriveAndAwaitAdvance();
            executor.shutdown();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
        }

        assertEquals(threads * perThread, counter.value());
    }
}
