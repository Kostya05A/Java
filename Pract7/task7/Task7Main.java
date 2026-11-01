package task7;

import src.Course;
import src.CourseFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Task7Main {
    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task7-");
        Path target = workDir.resolve("catalog.txt");
        Files.writeString(target, "старый каталог\n", StandardCharsets.UTF_8);

        Path damagedImport = workDir.resolve("damaged.txt");
        Files.writeString(damagedImport, """
                1|Java|40|10
                2||20|0
                3|Bad duration|0|0
                abc|Bad id|10|0
                4|Too much|10|10
                5|Good course|20|5
                """, StandardCharsets.UTF_8);

        ImportResult damagedResult = importCatalog(damagedImport);
        if (damagedResult instanceof ImportFailure failure) {
            System.out.println("Импорт отклонён целиком. Найдено ошибок: " + failure.errors().size());
            failure.errors().forEach(error ->
                    System.out.println("  строка " + error.line() + ": " + error.message()));
            System.out.println("Целевой каталог не изменён: "
                    + Files.readString(target, StandardCharsets.UTF_8).trim());
        } else {
            throw new IllegalStateException("Повреждённый импорт не должен публиковаться");
        }

        Path validImport = workDir.resolve("valid.txt");
        Files.writeString(validImport, """
                10|Потоки|40|10
                11|Форматы|20|0
                """, StandardCharsets.UTF_8);

        ImportResult validResult = importCatalog(validImport);
        if (validResult instanceof ImportSuccess success) {
            publish(target, success.courses());
            System.out.println("\nУспешный импорт опубликован целиком:");
            System.out.print(Files.readString(target, StandardCharsets.UTF_8));
        } else {
            throw new IllegalStateException("Корректный импорт должен завершиться успешно");
        }

        System.out.println();
        System.out.println("Контракт результата: ImportSuccess содержит весь каталог, ImportFailure не содержит курсов.");
        System.out.println("Выбран режим all-or-nothing: при любой ошибке импорт не публикует частичный результат.");
    }

    private static ImportResult importCatalog(Path source) throws IOException {
        List<Course> courses = new ArrayList<>();
        List<ImportError> errors = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    courses.add(parseLine(line));
                } catch (IllegalArgumentException e) {
                    errors.add(new ImportError(lineNumber, e.getMessage()));
                }
            }
        }

        if (!errors.isEmpty()) {
            return new ImportFailure(errors);
        }
        return new ImportSuccess(courses);
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

    private static void publish(Path target, List<Course> courses) throws IOException {
        StringBuilder text = new StringBuilder();
        for (Course course : courses) {
            text.append(course.id()).append('|')
                    .append(course.title()).append('|')
                    .append(course.durationHours()).append('|')
                    .append(course.completedHours()).append('\n');
        }
        Files.writeString(
                target,
                text.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    private sealed interface ImportResult permits ImportSuccess, ImportFailure {
    }

    private record ImportSuccess(List<Course> courses) implements ImportResult {
        private ImportSuccess {
            courses = List.copyOf(courses);
        }
    }

    private record ImportFailure(List<ImportError> errors) implements ImportResult {
        private ImportFailure {
            errors = List.copyOf(errors);
        }
    }

    private record ImportError(int line, String message) {
    }
}
