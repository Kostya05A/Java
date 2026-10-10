package level2;

import java.io.IOException;

public class LoggableResource implements AutoCloseable {

    private final String name;
    private final StringBuilder log;
    private final boolean failOnClose;
    private boolean closed;

    public LoggableResource(String name, StringBuilder log) {
        this(name, log, false);
    }

    public LoggableResource(String name, StringBuilder log, boolean failOnClose) {
        this.name = name;
        this.log = log;
        this.failOnClose = failOnClose;
        open();
    }

    private void open() {
        log.append(name).append(": open\n");
    }

    public void use(String operation) {
        if (closed) {
            throw new IllegalStateException(name + ": use() после close()");
        }
        log.append(name).append(": use(").append(operation).append(")\n");
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            log.append(name).append(": close() вызван повторно (close идемпотентен)\n");
            return;
        }
        closed = true;
        log.append(name).append(": close\n");
        if (failOnClose) {
            throw new IOException(name + ": сбой при закрытии (flush не прошёл)");
        }
    }
}
