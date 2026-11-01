package task5;

import src.CourseEntry;
import src.Level;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Task5Demo {
    public static void main(String[] args) {
        List<CourseEntry> courses = List.of(
                new CourseEntry("JAVA101", "Java: основы", Level.BEGINNER, 24),
                new CourseEntry("JAVA201", "Java: коллекции", Level.INTERMEDIATE, 32),
                new CourseEntry("ARCH401", "Архитектура сервисов", Level.ADVANCED, 48),
                new CourseEntry("DB301", "Оптимизация SQL", Level.ADVANCED, 48)
        );

        Optional<CourseEntry> longest = CourseSearch.longestCourse(courses);
        CourseView view = longest
                .map(CourseView::of)
                .orElse(new CourseView("N/A", "Курсы отсутствуют", 0));

        System.out.println("Самый длинный курс: " + view);
        longest.ifPresentOrElse(
                course -> System.out.println("  найден: " + course.code() + " / " + course.title()),
                () -> System.out.println("  поток был пуст"));

        List<CourseEntry> empty = List.of();
        Optional<CourseEntry> missing = CourseSearch.longestCourse(empty);
        String fallback = missing
                .map(course -> course.title().toUpperCase(Locale.ROOT))
                .orElseGet(() -> "fallback: курсы не найдены");

        System.out.println("\nПустой список:");
        System.out.println("  Optional пуст: " + missing.isEmpty());
        System.out.println("  map + orElseGet: " + fallback);
        System.out.println("  orElse: " + missing.map(CourseEntry::title).orElse("fallback: нет данных"));
        System.out.println("\nOptional.get не используется: наличие проверяется map/orElse/ifPresentOrElse.");
        System.out.println("Optional не хранится в CourseView: в сущности только обычные поля.");
    }
}
