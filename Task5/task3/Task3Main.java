package task3;

import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Task3Main {
    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task3-");
        Path source = workDir.resolve("source.txt");
        Path target = workDir.resolve("target.txt");
        Files.writeString(source, "pipeline", StandardCharsets.UTF_8);

        List<String> events = new ArrayList<>();

        try (TrackingReader in = new TrackingReader(
                     Files.newBufferedReader(source, StandardCharsets.UTF_8), "reader", events);
             TrackingWriter out = new TrackingWriter(
                     Files.newBufferedWriter(
                             target,
                             StandardCharsets.UTF_8,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING,
                             StandardOpenOption.WRITE),
                     "writer",
                     events)) {
            int value;
            while ((value = in.read()) != -1) {
                out.write(value);
            }
            out.flush();
            throw new IllegalStateException("Смоделированный сбой в середине pipeline");
        } catch (IllegalStateException e) {
            System.out.println("Исключение в теле try: " + e.getMessage());
        }

        System.out.println("События закрытия: " + events);
        if (!events.equals(List.of("close:writer", "close:reader"))) {
            throw new IllegalStateException("Неверный порядок закрытия ресурсов: " + events);
        }
        System.out.println("Оба ресурса закрыты, writer закрыт раньше reader: TWR закрывает в обратном порядке объявления.");
        System.out.println("Содержимое target после закрытия writer: " + Files.readString(target, StandardCharsets.UTF_8));
    }

    private static final class TrackingReader extends FilterReader {
        private final String name;
        private final List<String> events;
        private boolean closed;

        private TrackingReader(Reader in, String name, List<String> events) {
            super(in);
            this.name = name;
            this.events = events;
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                events.add("close:" + name);
                super.close();
            }
        }
    }

    private static final class TrackingWriter extends FilterWriter {
        private final String name;
        private final List<String> events;
        private boolean closed;

        private TrackingWriter(Writer out, String name, List<String> events) {
            super(out);
            this.name = name;
            this.events = events;
        }

        @Override
        public void close() throws IOException {
            if (!closed) {
                closed = true;
                events.add("close:" + name);
                super.close();
            }
        }
    }
}
