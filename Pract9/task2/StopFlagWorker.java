package task2;

public final class StopFlagWorker {

    /** Некорректный вариант: флаг не volatile — поток может не увидеть запись. */
    public static final class BrokenWorker implements Runnable {
        private boolean stop = false;
        private long spins = 0;

        public void stop() { stop = true; }
        public long spins() { return spins; }

        @Override public void run() {
            while (!stop) { spins++; }
        }
    }

    /** Корректный вариант: volatile обеспечивает видимость записи флага. */
    public static final class VolatileWorker implements Runnable {
        private volatile boolean stop = false;
        private long spins = 0;

        public void stop() { stop = true; }
        public long spins() { return spins; }

        @Override public void run() {
            while (!stop) { spins++; }
        }
    }

    private StopFlagWorker() {}
}
