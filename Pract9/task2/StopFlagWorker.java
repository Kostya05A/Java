package task2;

public final class StopFlagWorker {

    public interface StoppableWorker extends Runnable {
        void stop();

        long spins();
    }

    // Некорректный вариант: флаг не volatile — поток может не увидеть запись.
    public static final class BrokenWorker implements StoppableWorker {
        private boolean stop = false;
        private long spins = 0;

        @Override
        public void stop() {
            stop = true;
        }

        @Override
        public long spins() {
            return spins;
        }

        @Override
        public void run() {
            while (!stop && !Thread.currentThread().isInterrupted()) {
                spins++;
            }
        }
    }

    // Корректный вариант: volatile обеспечивает видимость записи флага.
    public static final class VolatileWorker implements StoppableWorker {
        private volatile boolean stop = false;
        private long spins = 0;

        @Override
        public void stop() {
            stop = true;
        }

        @Override
        public long spins() {
            return spins;
        }

        @Override
        public void run() {
            while (!stop && !Thread.currentThread().isInterrupted()) {
                spins++;
            }
        }
    }

    private StopFlagWorker() {
    }
}
