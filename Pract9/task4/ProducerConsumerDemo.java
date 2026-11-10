package task4;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ProducerConsumerDemo {
    private static final EnrollmentRequest POISON =
            new EnrollmentRequest("__POISON__", "__POISON__", "__POISON__");
    private static final long PRODUCERS_TIMEOUT_SECONDS = 30;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 30;
    private static final long FORCE_SHUTDOWN_TIMEOUT_SECONDS = 5;

    public record Report(int produced, int processed, Set<String> processedIds, boolean terminatedCleanly) {
    }

    private ProducerConsumerDemo() {
    }

    public static Report run(List<EnrollmentRequest> requests, int producerCount, int consumerCount,
                             int capacity, long processingDelayMillis) throws InterruptedException {
        if (producerCount < 1) {
            throw new IllegalArgumentException("producerCount должен быть >= 1");
        }
        if (consumerCount < 1) {
            throw new IllegalArgumentException("consumerCount должен быть >= 1");
        }
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity должен быть >= 1");
        }
        if (processingDelayMillis < 0) {
            throw new IllegalArgumentException("processingDelayMillis должен быть >= 0");
        }

        BlockingQueue<EnrollmentRequest> queue = new ArrayBlockingQueue<>(capacity);
        AtomicInteger processed = new AtomicInteger();
        Set<String> processedIds = ConcurrentHashMap.newKeySet();
        CountDownLatch producersDone = new CountDownLatch(producerCount);

        ExecutorService producerPool = Executors.newFixedThreadPool(
                producerCount, new NamedThreadFactory("coursehub-producer"));
        ExecutorService consumerPool = Executors.newFixedThreadPool(
                consumerCount, new NamedThreadFactory("coursehub-consumer"));

        boolean terminatedCleanly = false;
        try {
            for (int i = 0; i < consumerCount; i++) {
                consumerPool.execute(() -> consume(queue, processed, processedIds, processingDelayMillis));
            }

            for (List<EnrollmentRequest> chunk : split(requests, producerCount)) {
                producerPool.execute(() -> produce(queue, chunk, producersDone));
            }

            if (!producersDone.await(PRODUCERS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "Producers не завершились за " + PRODUCERS_TIMEOUT_SECONDS + " секунд");
            }

            for (int i = 0; i < consumerCount; i++) {
                queue.put(POISON);
            }

            consumerPool.shutdown();
            producerPool.shutdown();

            boolean consumersStopped = consumerPool.awaitTermination(
                    SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            boolean producersStopped = producerPool.awaitTermination(
                    SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            terminatedCleanly = consumersStopped && producersStopped;
            if (!terminatedCleanly) {
                consumerPool.shutdownNow();
                producerPool.shutdownNow();
            }
        } finally {
            consumerPool.shutdownNow();
            producerPool.shutdownNow();
            consumerPool.awaitTermination(FORCE_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            producerPool.awaitTermination(FORCE_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        return new Report(requests.size(), processed.get(), Set.copyOf(processedIds), terminatedCleanly);
    }

    private static void consume(BlockingQueue<EnrollmentRequest> queue, AtomicInteger processed,
                                Set<String> processedIds, long processingDelayMillis) {
        try {
            while (true) {
                EnrollmentRequest request = queue.take();
                if (request == POISON) {
                    return;
                }
                if (processingDelayMillis > 0) {
                    Thread.sleep(processingDelayMillis);
                }
                if (processedIds.add(request.id())) {
                    processed.incrementAndGet();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void produce(BlockingQueue<EnrollmentRequest> queue, List<EnrollmentRequest> chunk,
                                CountDownLatch producersDone) {
        try {
            for (EnrollmentRequest request : chunk) {
                queue.put(request);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            producersDone.countDown();
        }
    }

    private static List<List<EnrollmentRequest>> split(List<EnrollmentRequest> requests, int parts) {
        List<List<EnrollmentRequest>> chunks = new ArrayList<>(parts);
        for (int i = 0; i < parts; i++) {
            chunks.add(new ArrayList<>());
        }
        for (int i = 0; i < requests.size(); i++) {
            chunks.get(i % parts).add(requests.get(i));
        }
        return chunks;
    }

    public static void main(String[] args) throws InterruptedException {
        List<EnrollmentRequest> requests = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            requests.add(new EnrollmentRequest(
                    "req-" + i, "student-" + (i % 40), "COURSE-" + (i % 8)));
        }

        int cores = Runtime.getRuntime().availableProcessors();
        int producers = Math.max(1, cores / 2);
        int consumers = Math.max(2, cores * 2);
        Report report = run(requests, producers, consumers, 8, 1);

        System.out.println("Producer-consumer CourseHub");
        System.out.println("cores=" + cores + ", producers=" + producers
                + ", consumers=" + consumers + " (blocking-сценарий)");
        System.out.println("produced=" + report.produced()
                + ", processed=" + report.processed()
                + ", unique=" + report.processedIds().size()
                + ", terminatedCleanly=" + report.terminatedCleanly());
        explain();
    }

    private static void explain() {
        System.out.println("\nПочему так:");
        System.out.println("  - ArrayBlockingQueue(8) ограничивает буфер: когда он полон, producer.put");
        System.out.println("    блокируется, пока consumer не заберёт заявку. Это backpressure без busy waiting.");
        System.out.println("  - Завершение: producers сигналят CountDownLatch, затем в очередь кладутся");
        System.out.println("    poison pills по одной на consumer; take() просыпается и consumer выходит штатно.");
        System.out.println("  - Пул: producers и consumers разнесены по ExecutorService с именами потоков");
        System.out.println("    coursehub-producer-* и coursehub-consumer-*; shutdown + awaitTermination,");
        System.out.println("    при превышении таймаута выполняется shutdownNow.");
        System.out.println("  - CPU-bound: pool ≈ числу ядер; blocking (I/O, ожидание): pool больше,");
        System.out.println("    ориентир threads ≈ cores * (1 + wait/compute). Consumers имитируют I/O.");
    }

    private static final class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger sequence = new AtomicInteger();

        private NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, prefix + "-" + sequence.incrementAndGet());
            thread.setDaemon(false);
            return thread;
        }
    }
}
