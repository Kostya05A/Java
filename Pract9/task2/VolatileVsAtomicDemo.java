package task2;

public final class VolatileVsAtomicDemo {

    /**
     * volatile-счётчик: инкремент — составная операция (read-modify-write).
     * volatile даёт видимость, но НЕ атомарность -> возможны потери.
     */
    private static volatile int volatileCounter = 0;

    public static void main(String[] args) throws InterruptedException {
        int threads = 8, perThread = 50_000;
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) volatileCounter++; // НЕ атомарно
            });
        }
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();

        System.out.println("volatile++ expected=" + (threads * perThread)
                + " actual=" + volatileCounter);
    }
}