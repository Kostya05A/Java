package task8;

import src.Course;
import src.CourseFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Task8Main {
    private static final int LINES_TO_GENERATE = 1_000_000;
    private static final int MAX_ERRORS = 5;
    private static final long CANCEL_AFTER_INVALID = 50;

    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task8-");
        Path source = workDir.resolve("large-catalog.txt");
        generateLargeFile(source);

        StreamImportResult result = streamImport(source, MAX_ERRORS, CANCEL_AFTER_INVALID);

        System.out.println("Размер файла: " + Files.size(source) + " байт");
        System.out.println("Всего строк в файле: " + LINES_TO_GENERATE);
        System.out.println("Обработано строк: " + result.linesRead());
        System.out.println("Принято курсов: " + result.accepted());
        System.out.println("Отклонено строк: " + result.rejected());
        System.out.println("Сохранено ошибок: " + result.errors().size()
                + " (лимит " + MAX_ERRORS + ")");
        result.errors().forEach(error ->
                System.out.println("  строка " + error.line() + ": " + error.message()));
        System.out.println("Импорт отменён: " + result.cancelled());
        System.out.println("Причина: " + result.reason());
        System.out.println();
        System.out.println("Файл читается построчно через BufferedReader, без readAllLines.");
        System.out.println("Память ограничена: хранятся только счётчики и не более " + MAX_ERRORS + " ошибок.");
        System.out.println("Обработка прекращается после " + CANCEL_AFTER_INVALID + " отклонённых строк.");
    }

    private static StreamImportResult streamImport(Path source, int maxErrors, long cancelAfterInvalid)
            throws IOException {
        long linesRead = 0;
        long accepted = 0;
        long rejected = 0;
        List<ImportError> errors = new ArrayList<>();
        boolean cancelled = false;
        String reason = "";

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                linesRead++;
                try {
                    parseLine(line);
                    accepted++;
                } catch (IllegalArgumentException e) {
                    rejected++;
                    if (errors.size() < maxErrors) {
                        errors.add(new ImportError(linesRead, e.getMessage()));
                    }
                    if (rejected >= cancelAfterInvalid) {
                        cancelled = true;
                        reason = "достигнут порог отклонённых строк: " + cancelAfterInvalid;
                        break;
                    }
                }
            }
        }

        return new StreamImportResult(
                linesRead,
                accepted,
                rejected,
                List.copyOf(errors),
                cancelled,
                reason);
    }

    private static Course parseLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException("ожидалось 4 поля, найдено " + parts.length);
        }
        try {
            long id = Long.parseLong(parts[0]);
            String title = parts[1];
            int duration = Integer.parseInt(parts[2]);
            int completed = Integer.parseInt(parts[3]);

            if (id <= 0) {
                throw new IllegalArgumentException("id должен быть положительным");
            }
            if (title.isBlank()) {
                throw new IllegalArgumentException("название не может быть пустым");
            }
            if (duration < 1 || duration > 100) {
                throw new IllegalArgumentException("duration должен быть от 1 до 100");
            }
            if (completed < 0 || completed >= duration) {
                throw new IllegalArgumentException("completed должен быть от 0 до duration - 1");
            }
            return CourseFactory.create(id, title, duration, completed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("неверный числовой формат", e);
        }
    }

    private static void generateLargeFile(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (int line = 1; line <= LINES_TO_GENERATE; line++) {
                if (line % 100 == 0) {
                    writer.write(line + "|broken|not-a-number|0");
                } else {
                    writer.write(line + "|Course " + line + "|40|10");
                }
                writer.newLine();
            }
        }
    }

    private record ImportError(long line, String message) {
    }

    private record StreamImportResult(
            long linesRead,
            long accepted,
            long rejected,
            List<ImportError> errors,
            boolean cancelled,
            String reason) {

        private StreamImportResult {
            errors = List.copyOf(errors);
        }
    }
}
