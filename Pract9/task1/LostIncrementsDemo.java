package task1;

import src.Counter;

import java.util.concurrent.CountDownLatch;

public final class LostIncrementsDemo {
    private LostIncrementsDemo() {
    }

    public static void main(String[] args) throws InterruptedException {
        int threads = 8;
        int perThread = 50_000;
        int expected = threads * perThread;

        Counter unsafe = new Counter();
        run(unsafe::unsafeIncrement, threads, perThread);
        System.out.println("unsafe expected=" + expected + " actual=" + unsafe.value()
                + " lost=" + (expected - unsafe.value()));

        Counter sync = new Counter();
        run(sync::syncIncrement, threads, perThread);
        System.out.println("sync   expected=" + expected + " actual=" + sync.value());

        Counter block = new Counter();
        run(block::blockIncrement, threads, perThread);
        System.out.println("block  expected=" + expected + " actual=" + block.value());
    }

    private static void run(Runnable operation, int threads, int perThread) throws InterruptedException {
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
            }, "worker-" + i);
            workers[i].start();
        }
        start.countDown();
        for (Thread worker : workers) {
            worker.join();
        }
    }
}
