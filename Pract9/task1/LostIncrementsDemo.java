package task1;

import src.Counter;

public final class LostIncrementsDemo {
    private LostIncrementsDemo() {}

    public static void main(String[] args) throws InterruptedException {
        int threads = 8;
        int perThread = 50_000;

        Counter unsafe = new Counter();
        run(unsafe::unsafeIncrement, threads, perThread);
        System.out.println("unsafe expected=" + (threads * perThread) + " actual=" + unsafe.value());

        Counter sync = new Counter();
        run(sync::syncIncrement, threads, perThread);
        System.out.println("sync   expected=" + (threads * perThread) + " actual=" + sync.value());

        Counter block = new Counter();
        run(block::blockIncrement, threads, perThread);
        System.out.println("block  expected=" + (threads * perThread) + " actual=" + block.value());
    }

    private static void run(Runnable op, int threads, int perThread) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) op.run();
            }, "worker-" + i);
            ts[i].start();
        }
        for (Thread t : ts) t.join();
    }
}
