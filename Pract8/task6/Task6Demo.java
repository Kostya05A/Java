package task6;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task6Demo {
    public static void main(String[] args) {
        List<Integer> durations = List.of(24, 32, 24, 48, 40, 20, 60, 28, 50, 36);

        Stats sequential = durations.stream().collect(StatsCollector.STATS);
        Stats parallel = durations.parallelStream().collect(StatsCollector.STATS);

        StatsAccumulator left = new StatsAccumulator();
        StatsAccumulator right = new StatsAccumulator();
        durations.subList(0, 4).forEach(left::accept);
        durations.subList(4, durations.size()).forEach(right::accept);
        Stats combined = left.merge(right).finish();

        IntSummaryStatistics builtIn = durations.stream()
                .collect(Collectors.summarizingInt(Integer::intValue));

        System.out.println("Длительности: " + durations);
        System.out.println("Свой collector (sequential): " + sequential
                + ", average=" + String.format(Locale.ROOT, "%.2f", sequential.average()));
        System.out.println("Свой collector (parallel):   " + parallel);
        System.out.println("Ручное разбиение + combiner:  " + combined);
        System.out.println("parallel == sequential: " + parallel.equals(sequential));
        System.out.println("combiner корректен: " + combined.equals(sequential));
        System.out.println();
        System.out.println("Готовый summarizingInt: count=" + builtIn.getCount()
                + ", sum=" + builtIn.getSum()
                + ", min=" + builtIn.getMin()
                + ", max=" + builtIn.getMax()
                + ", average=" + String.format(Locale.ROOT, "%.2f", builtIn.getAverage()));
        System.out.println("Совпадение со своим collector: "
                + (sequential.count() == builtIn.getCount()
                        && sequential.sum() == builtIn.getSum()
                        && sequential.min() == builtIn.getMin()
                        && sequential.max() == builtIn.getMax()));
        System.out.println();
        System.out.println("Мутабельное состояние живёт в StatsAccumulator, который создаёт supplier");
        System.out.println("на каждый collect; static mutable state отсутствует.");
        System.out.println("Combiner складывает независимые части разбиения, finisher возвращает immutable record Stats.");

        Stats empty = Stream.<Integer>empty().collect(StatsCollector.STATS);
        System.out.println("Пустой поток: " + empty
                + " (min/max = 0, а не Integer.MAX_VALUE/MIN_VALUE).");
    }
}
