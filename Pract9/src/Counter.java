package src;

/** Общий счётчик с неатомарным и атомарным инкрементом. */
public final class Counter {
    private int value;          // защищаемый инвариант: value >= 0 и отражает число инкрементов

    /** Небезопасный вариант: counter++ разложен на чтение, вычисление, запись. */
    public void unsafeIncrement() {
        int read = value;       // чтение
        int next = read + 1;    // вычисление
        value = next;           // запись
    }

    /** Синхронизированный метод: монитор — this. */
    public synchronized void syncIncrement() {
        value++;
    }

    /** Минимальный синхронизированный блок: тот же монитор this, но только критическая секция. */
    public void blockIncrement() {
        synchronized (this) {
            value++;
        }
    }

    public synchronized int value() {
        return value;
    }
}
