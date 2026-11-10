package task2;

import java.util.concurrent.atomic.AtomicInteger;

public final class VolatileVsAtomicDemo {

     // volatile-счётчик: инкремент — составная операция (read-modify-write).
     // volatile даёт видимость, но НЕ атомарность -> возможны потери.
    private static volatile int volatileCounter = 0;
    private static final AtomicInteger atomicCounter = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        run();
    }

    public static void run() throws InterruptedException {
        int threads = 8;
        int perThread = 50_000;
        volatileCounter = 0;
        atomicCounter.set(0);

        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    volatileCounter++;
                    atomicCounter.incrementAndGet();
                }
            }, "counter-worker-" + i);
        }
        for (Thread t : ts) {
            t.start();
        }
        for (Thread t : ts) {
            t.join();
        }

        int expected = threads * perThread;
        System.out.println("volatile++ expected=" + expected + " actual=" + volatileCounter);
        System.out.println("AtomicInteger expected=" + expected + " actual=" + atomicCounter.get());
    }
}
