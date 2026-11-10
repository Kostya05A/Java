package task2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Task2Demo {
    private static final long STOP_TIMEOUT_SECONDS = 2;

    private Task2Demo() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Флаг остановки и visibility");
        runStopFlag("BrokenWorker (без volatile)", new StopFlagWorker.BrokenWorker());
        runStopFlag("VolatileWorker (volatile)", new StopFlagWorker.VolatileWorker());

        System.out.println("\nvolatile vs AtomicInteger");
        VolatileVsAtomicDemo.run();
        explain();
    }

    private static void runStopFlag(String label, StopFlagWorker.StoppableWorker worker) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, label);
            thread.setDaemon(true);
            return thread;
        });
        try {
            Future<?> future = executor.submit(worker);
            Thread.sleep(50);
            worker.stop();
            try {
                future.get(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                System.out.println(label + ": остановился, spins=" + worker.spins());
            } catch (TimeoutException e) {
                System.out.println(label + ": не увидел stop за "
                        + STOP_TIMEOUT_SECONDS + " c (проблема видимости)");
            }
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

    private static void explain() {
        System.out.println("\nПочему так:");
        System.out.println("  - BrokenWorker.stop — обычное поле: JIT может закэшировать чтение в цикле,");
        System.out.println("    поэтому запись из другого потока не обязана быть видимой.");
        System.out.println("    На конкретной JVM он может случайно остановиться, но гарантии нет.");
        System.out.println("  - VolatileWorker.stop — volatile: запись видна читающему потоку, чтение не выносится.");
        System.out.println("  - volatile даёт видимость, но не атомарность: volatile++ всё ещё read-modify-write.");
        System.out.println("  - Для атомарного счётчика нужен AtomicInteger или synchronized.");
    }
}
