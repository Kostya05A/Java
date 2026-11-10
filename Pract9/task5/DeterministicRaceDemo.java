package task5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public final class DeterministicRaceDemo {
    private DeterministicRaceDemo() {
    }

    public static void main(String[] args) throws Exception {
        demonstrateLostUpdate();
        demonstrateSynchronizedCounter();
        explain();
    }

    private static void demonstrateLostUpdate() throws Exception {
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
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        System.out.println("Детерминированная гонка: ожидалось 2, получено " + counter.value());
    }

    private static void demonstrateSynchronizedCounter() throws Exception {
        int threads = 8;
        int perThread = 20_000;
        ProtocolCounter counter = new ProtocolCounter();
        Phaser start = new Phaser(threads + 1);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    start.arriveAndAwaitAdvance();
                    for (int j = 0; j < perThread; j++) {
                        counter.syncIncrement();
                    }
                    return null;
                }));
            }

            start.arriveAndAwaitAdvance();
            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        int expected = threads * perThread;
        System.out.println("synchronized: ожидалось " + expected + ", получено " + counter.value());
    }

    private static void explain() {
        System.out.println("\nПочему так:");
        System.out.println("  - Phaser — точка протокола: обе нити читают value до барьера, затем обе пишут read + 1.");
        System.out.println("  - Порядок фиксирован барьером, поэтому потеря ровно одного инкремента воспроизводима.");
        System.out.println("  - Thread.sleep как средство синхронизации не используется.");
        System.out.println("  - Executor освобождается в finally через shutdownNow + awaitTermination.");
    }
}
