package task2;

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

public class Task2Main {
    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task2-");
        Path catalog = workDir.resolve("courses.txt");

        List<Course> courses = List.of(
                CourseFactory.create(1, "Потоки ввода-вывода", 32, 8),
                CourseFactory.create(2, "Кодировки символов", 16, 0),
                CourseFactory.create(3, "Buffered Reader и Writer", 24, 6)
        );

        writeCatalog(catalog, courses);
        Report report = readCatalog(catalog);

        System.out.println("Прочитано через BufferedReader (UTF-8):");
        report.courses().forEach(course -> System.out.printf(
                "  id=%d, title=%s, duration=%d, completed=%d%n",
                course.id(), course.title(), course.durationHours(), course.completedHours()));
        System.out.println("Строк: " + report.lines());
        System.out.println("Символов без переводов строк: " + report.characters());
        System.out.println("Байтов в файле: " + Files.size(catalog));
        System.out.println();
        System.out.println("Граница bytes -> chars: файл хранит байты UTF-8.");
        System.out.println("BufferedReader декодирует байты в Java-символы (UTF-16) и только затем readLine ищет перевод строки.");
        System.out.println("Кириллица в UTF-8 занимает 2 байта на символ, поэтому байтов больше, чем символов.");
        System.out.println("Граница проходит внутри InputStreamReader/CharsetDecoder: многобайтовый символ может быть разрезан между буферами чтения.");
    }

    private static void writeCatalog(Path path, List<Course> courses) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                path,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {
            for (Course course : courses) {
                writer.write(course.id() + "|" + course.title() + "|"
                        + course.durationHours() + "|" + course.completedHours());
                writer.newLine();
            }
        }
    }

    private static Report readCatalog(Path path) throws IOException {
        List<Course> courses = new ArrayList<>();
        int lines = 0;
        long characters = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                characters += line.length();
                courses.add(parseLine(line, lines));
            }
        }
        return new Report(List.copyOf(courses), lines, characters);
    }

    private static Course parseLine(String line, int lineNumber) throws IOException {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 4) {
            throw new IOException("Строка " + lineNumber + ": ожидалось 4 поля");
        }
        try {
            long id = Long.parseLong(parts[0]);
            String title = parts[1];
            int duration = Integer.parseInt(parts[2]);
            int completed = Integer.parseInt(parts[3]);
            if (id <= 0 || title.isBlank() || duration < 1 || duration > 100
                    || completed < 0 || completed >= duration) {
                throw new IOException("Строка " + lineNumber + ": некорректные данные курса");
            }
            return CourseFactory.create(id, title, duration, completed);
        } catch (NumberFormatException e) {
            throw new IOException("Строка " + lineNumber + ": неверный числовой формат", e);
        }
    }

    private record Report(List<Course> courses, int lines, long characters) {
    }
}
