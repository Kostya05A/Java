package task6;

import java.util.stream.Collector;

public final class StatsCollector {
    public static final Collector<Integer, StatsAccumulator, Stats> STATS = Collector.of(
            StatsAccumulator::new,
            StatsAccumulator::accept,
            StatsAccumulator::merge,
            StatsAccumulator::finish,
            Collector.Characteristics.UNORDERED);

    private StatsCollector() {
    }
}
