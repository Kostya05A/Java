package task1;

import src.CourseEntry;
import src.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Task1Demo {
    public static void main(String[] args) {
        List<CourseEntry> source = List.of(
                new CourseEntry("JAVA201", "Java: коллекции", Level.INTERMEDIATE, 32),
                new CourseEntry("JAVA101", "Java: основы", Level.BEGINNER, 24),
                new CourseEntry("DB101", "SQL: запросы", Level.BEGINNER, 24),
                new CourseEntry("WEB101", "HTTP и REST", Level.BEGINNER, 24),
                new CourseEntry("ARCH401", "Архитектура сервисов", Level.ADVANCED, 48),
                new CourseEntry("JAVA301", "Java: конкурентность", Level.ADVANCED, 48),
                new CourseEntry("TEST201", "Автотесты", Level.INTERMEDIATE, 32),
                new CourseEntry("DB301", "Оптимизация SQL", Level.ADVANCED, 40)
        );

        List<CourseEntry> reversed = new ArrayList<>(source);
        Collections.reverse(reversed);

        List<CourseEntry> shuffled = new ArrayList<>(source);
        Collections.shuffle(shuffled, new Random(42));

        List<CourseEntry> sortedSource = CourseSorter.sorted(source);
        List<CourseEntry> sortedReversed = CourseSorter.sorted(reversed);
        List<CourseEntry> sortedShuffled = CourseSorter.sorted(shuffled);

        System.out.println("Компаратор: " + CourseSorter.orderDescription());
        System.out.println("Исходный порядок:");
        print(source);
        System.out.println("\nПосле сортировки:");
        print(sortedSource);

        boolean deterministic = sortedSource.equals(sortedReversed)
                && sortedSource.equals(sortedShuffled);
        System.out.println("\nРезультат одинаков для трёх разных исходных порядков: " + deterministic);
        if (!deterministic) {
            throw new AssertionError("Полный компаратор должен давать одинаковый порядок");
        }

        List<CourseEntry> unstableSource = CourseSorter.sortedWithoutTieBreakers(source);
        List<CourseEntry> unstableShuffled = CourseSorter.sortedWithoutTieBreakers(shuffled);
        boolean inputDependent = !unstableSource.equals(unstableShuffled);
        System.out.println("Без tie-breakers code/title порядок зависит от входа: " + inputDependent);
        System.out.println("Поэтому code и title — явные tie-breakers, а не случайная стабильность сортировки.");
    }

    private static void print(List<CourseEntry> courses) {
        for (CourseEntry course : courses) {
            System.out.printf("  %-8s %-12s %3d ч  %s%n",
                    course.code(), course.level(), course.durationHours(), course.title());
        }
    }
}
