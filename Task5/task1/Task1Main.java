package task1;

import src.Course;
import src.CourseFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Task1Main {
    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task1-");
        Path catalog = workDir.resolve("courses.txt");

        List<Course> courses = List.of(
                CourseFactory.create(1, "Java: потоки и файлы", 40, 12),
                CourseFactory.create(2, "Файловые форматы", 24, 4),
                CourseFactory.create(3, "Сетевой ввод-вывод", 30, 0)
        );

        saveCatalog(catalog, courses);
        List<Course> loaded = loadCatalog(catalog);

        System.out.println("Сохранено и прочитано через Files.writeString/readString (UTF-8):");
        loaded.forEach(course -> System.out.printf(
                "  id=%d, title=%s, duration=%d, completed=%d%n",
                course.id(), course.title(), course.durationHours(), course.completedHours()));
        System.out.println("Размер файла в байтах: " + Files.size(catalog));

        expectLoadFailure("отсутствующий файл", workDir.resolve("missing.txt"));
        expectLoadFailure("каталог вместо файла", workDir);
    }

    private static void saveCatalog(Path path, List<Course> courses) throws IOException {
        StringBuilder text = new StringBuilder();
        for (Course course : courses) {
            String title = course.title();
            if (title.indexOf('|') >= 0 || title.indexOf('\n') >= 0 || title.indexOf('\r') >= 0) {
                throw new IllegalArgumentException("Название курса содержит недопустимый символ: " + title);
            }
            text.append(course.id()).append('|')
                    .append(title).append('|')
                    .append(course.durationHours()).append('|')
                    .append(course.completedHours()).append('\n');
        }

        Files.writeString(
                path,
                text.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    private static List<Course> loadCatalog(Path path) throws CatalogException {
        if (Files.isDirectory(path)) {
            throw new CatalogException("Ожидался файл, но по пути находится каталог: " + path);
        }

        String text;
        try {
            text = Files.readString(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw new CatalogException("Файл каталога не найден: " + path, e);
        } catch (IOException e) {
            throw new CatalogException("Не удалось прочитать каталог: " + path, e);
        }

        List<Course> result = new ArrayList<>();
        String[] lines = text.split("\\R", -1);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                continue;
            }
            result.add(parseLine(lines[i], i + 1));
        }
        return List.copyOf(result);
    }

    private static Course parseLine(String line, int lineNumber) throws CatalogException {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new CatalogException("Строка " + lineNumber + ": ожидалось 4 поля, найдено " + parts.length);
        }
        try {
            long id = Long.parseLong(parts[0]);
            String title = parts[1];
            int duration = Integer.parseInt(parts[2]);
            int completed = Integer.parseInt(parts[3]);
            validate(id, title, duration, completed, lineNumber);
            return CourseFactory.create(id, title, duration, completed);
        } catch (NumberFormatException e) {
            throw new CatalogException("Строка " + lineNumber + ": число имеет неверный формат", e);
        }
    }

    private static void validate(long id, String title, int duration, int completed, int lineNumber)
            throws CatalogException {
        if (id <= 0) {
            throw new CatalogException("Строка " + lineNumber + ": id должен быть положительным");
        }
        if (title.isBlank()) {
            throw new CatalogException("Строка " + lineNumber + ": название не может быть пустым");
        }
        if (duration < 1 || duration > 100) {
            throw new CatalogException("Строка " + lineNumber + ": duration должен быть от 1 до 100");
        }
        if (completed < 0 || completed >= duration) {
            throw new CatalogException("Строка " + lineNumber + ": completed должен быть от 0 до duration - 1");
        }
    }

    private static void expectLoadFailure(String label, Path path) throws IOException {
        boolean failed = false;
        try {
            loadCatalog(path);
        } catch (CatalogException e) {
            failed = true;
            System.out.println("\n" + label + " -> ожидаемая ошибка без ложного успеха:");
            System.out.println("  " + e.getMessage());
        }
        if (!failed) {
            throw new IllegalStateException("Ожидалась ошибка для сценария: " + label);
        }
    }

    private static final class CatalogException extends IOException {
        private CatalogException(String message) {
            super(message);
        }

        private CatalogException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
