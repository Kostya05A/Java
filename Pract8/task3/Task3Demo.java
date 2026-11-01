package task3;

import src.CourseEntry;
import src.Level;

import java.util.List;
import java.util.stream.Stream;

public class Task3Demo {
    public static void main(String[] args) {
        List<CourseEntry> courses = List.of(
                new CourseEntry("JAVA101", "Java: основы", Level.BEGINNER, 24, 24),
                new CourseEntry("DB101", "SQL: запросы", Level.BEGINNER, 20, 5),
                new CourseEntry("WEB101", "HTTP и REST", Level.BEGINNER, 30, 10),
                new CourseEntry("ARCH401", "Архитектура сервисов", Level.ADVANCED, 48, 12)
        );

        DurationTotals correct = DurationAggregator.sumSequential(courses);
        DurationTotals parallel = DurationAggregator.sumParallel(courses);

        DurationTotals wrongIdentity = new DurationTotals(100, 40, 1);
        DurationTotals wrong = DurationAggregator.sumWithIdentity(courses, wrongIdentity);
        DurationTotals wrongOnEmpty = Stream.<DurationTotals>empty()
                .reduce(wrongIdentity, DurationTotals::combine);

        DurationTotals a = new DurationTotals(10, 2, 1);
        DurationTotals b = new DurationTotals(20, 5, 1);
        DurationTotals c = new DurationTotals(30, 7, 1);

        boolean associative = a.combine(b).combine(c).equals(a.combine(b.combine(c)));
        boolean leftIdentity = DurationTotals.IDENTITY.combine(a).equals(a);
        boolean rightIdentity = a.combine(DurationTotals.IDENTITY).equals(a);

        System.out.println("Курсы: " + courses);
        System.out.println("Правильная identity: " + DurationTotals.IDENTITY);
        System.out.println("Сумма (sequential): " + correct);
        System.out.println("Сумма (parallel):   " + parallel);
        System.out.println("parallel == sequential: " + parallel.equals(correct));
        System.out.println();
        System.out.println("Неправильная identity: " + wrongIdentity);
        System.out.println("Сумма с неправильной identity: " + wrong);
        System.out.println("Ожидалось: " + correct);
        System.out.println("Сдвиг: duration +" + (wrong.totalDuration() - correct.totalDuration())
                + ", completed +" + (wrong.totalCompleted() - correct.totalCompleted())
                + ", courseCount +" + (wrong.courseCount() - correct.courseCount()));
        System.out.println("На пустом потоке неправильная identity возвращает: " + wrongOnEmpty);
        System.out.println("Правильная identity на пустом потоке возвращает: "
                + Stream.<DurationTotals>empty().reduce(DurationTotals.IDENTITY, DurationTotals::combine));
        System.out.println();
        System.out.println("Ассоциативность (a (+) b) + c == a (+) (b (+) c): " + associative);
        System.out.println("Левая identity e (+) x == x: " + leftIdentity);
        System.out.println("Правая identity x (+) e == x: " + rightIdentity);
        System.out.println("Accumulator: (acc, course) -> acc.combine(course); combiner: combine;");
        System.out.println("combine ассоциативен и коммутативен, поэтому параллельное разбиение корректно.");
    }
}
