package task4;

import src.CourseEntry;
import src.Level;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Task4Demo {
    public static void main(String[] args) {
        List<CourseEntry> courses = List.of(
                new CourseEntry("JAVA101", "Java: основы", Level.BEGINNER, 24),
                new CourseEntry("DB101", "SQL: запросы", Level.BEGINNER, 20),
                new CourseEntry("WEB101", "HTTP и REST", Level.BEGINNER, 30),
                new CourseEntry("JAVA201", "Java: коллекции", Level.INTERMEDIATE, 32),
                new CourseEntry("TEST201", "Автотесты", Level.INTERMEDIATE, 28),
                new CourseEntry("JAVA301", "Java: конкурентность", Level.ADVANCED, 48),
                new CourseEntry("ARCH401", "Архитектура сервисов", Level.ADVANCED, 60),
                new CourseEntry("DB301", "Оптимизация SQL", Level.ADVANCED, 40)
        );

        Map<Level, LevelSummary> summaries = LevelSummarizer.summarize(courses);

        System.out.println("Сводка по level:");
        for (Level level : Level.values()) {
            LevelSummary summary = summaries.get(level);
            if (summary == null) {
                System.out.printf("  %-13s count=0, average=0.00%n", level);
            } else {
                System.out.printf(Locale.ROOT, "  %-13s count=%d, average=%.2f%n",
                        level, summary.count(), summary.averageDuration());
            }
        }

        long totalCount = summaries.values().stream()
                .mapToLong(LevelSummary::count)
                .sum();
        double totalAverage = courses.stream()
                .mapToInt(CourseEntry::durationHours)
                .average()
                .orElse(0.0);

        System.out.println("\nВсего курсов: " + totalCount
                + ", средняя длительность: " + String.format(Locale.ROOT, "%.2f", totalAverage));
        System.out.println("По курсам выполнен один проход: teeing передаёт каждый элемент");
        System.out.println("и counting, и averagingInt. Второй проход идёт только по Map уровней (не по данным).");
    }
}
