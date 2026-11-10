package task5;

import java.util.concurrent.Phaser;

public final class ProtocolCounter {
    private int value;

    public void unsafeIncrementWithProtocol(Phaser protocol) {
        int read = value;
        protocol.arriveAndAwaitAdvance();
        value = read + 1;
    }

    public synchronized void syncIncrement() {
        value++;
    }

    public synchronized int value() {
        return value;
    }
}
