package task7;

import src.Level;

import java.util.List;
import java.util.Locale;

public class Task7Demo {
    public static void main(String[] args) {
        List<CatalogEntry> catalog = List.of(
                new CatalogEntry("JAVA101", Level.BEGINNER, 24, 70),
                new CatalogEntry("JAVA201", Level.INTERMEDIATE, 36, 55),
                new CatalogEntry("JAVA301", Level.ADVANCED, 48, 40),
                new CatalogEntry("DB101", Level.BEGINNER, 20, 65),
                new CatalogEntry("WEB101", Level.BEGINNER, 30, 50),
                new CatalogEntry("ARCH401", Level.EXPERT, 60, 25),
                new CatalogEntry("TEST101", Level.INTERMEDIATE, 28, 60),
                new CatalogEntry("SEC401", Level.EXPERT, 50, 35)
        );

        CourseHubReport report = CourseHubAnalyzer.process(
                CourseEventGenerator.generate(catalog));
        printReport(report);
        explain();
    }

    private static void printReport(CourseHubReport report) {
        System.out.println("Однопроходный отчёт CourseHub");
        System.out.println("Всего событий: " + report.totalEvents());
        System.out.println("Валидных: " + report.validEvents()
                + ", аномалий: " + report.invalidEvents());
        System.out.println("Уникальных студентов: " + report.uniqueStudents());

        System.out.println("\nCompletion rate по курсам:");
        for (CourseCompletion course : report.completionByCourse()) {
            System.out.printf(Locale.ROOT,
                    "  %-8s enrollments=%6d, completed=%6d, rate=%5.1f%%%n",
                    course.code(),
                    course.enrollments(),
                    course.completions(),
                    course.completionRate() * 100);
        }

        System.out.println("\nTop-3 по completion rate (tie-breakers: enrollments desc, code asc):");
        int place = 1;
        for (CourseCompletion course : report.top3()) {
            System.out.printf(Locale.ROOT,
                    "  %d. %-8s rate=%5.1f%%, enrollments=%d%n",
                    place++,
                    course.code(),
                    course.completionRate() * 100,
                    course.enrollments());
        }

        System.out.println("\nРаспределение по level:");
        for (LevelShare share : report.distributionByLevel()) {
            System.out.printf(Locale.ROOT,
                    "  %-13s enrollments=%6d, share=%5.1f%%%n",
                    share.level(),
                    share.enrollments(),
                    share.share() * 100);
        }

        System.out.println("\nАномалии (лимит " + CourseHubAnalyzer.MAX_ANOMALIES + "):");
        for (Anomaly anomaly : report.anomalies()) {
            System.out.printf("  #%d student=%s course=%s reason=%s%n",
                    anomaly.sequence(),
                    anomaly.studentId(),
                    anomaly.courseCode(),
                    anomaly.reason());
        }
        System.out.println("Список аномалий усечён: " + report.anomaliesTruncated());
    }

    private static void explain() {
        System.out.println("\nПочему так:");
        System.out.println("  - Поток потреблён ровно один раз: Iterator получен из Stream и пройден циклом.");
        System.out.println("  - Память ограничена: Set студентов O(уникальных), Map курсов O(курсов),");
        System.out.println("    EnumMap уровней O(4), список аномалий ≤ " + CourseHubAnalyzer.MAX_ANOMALIES + ".");
        System.out.println("  - Детерминизм: TreeMap курсов, EnumMap уровней, top-3 с tie-breakers");
        System.out.println("    (rate desc, enrollments desc, code asc), аномалии в порядке поступления.");
        System.out.println("  - Side effects отсутствуют: process() ничего не печатает и не меняет");
        System.out.println("    внешнее состояние, а возвращает immutable report.");
        System.out.println("  - Stream API уместен для генерации событий, финальной сортировки top-3");
        System.out.println("    и преобразования Map -> List.");
        System.out.println("  - Обычный цикл яснее для единственного прохода с несколькими агрегатами:");
        System.out.println("    teeing/collectors для этого сделали бы код заметно сложнее.");
    }
}
