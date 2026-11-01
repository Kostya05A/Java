package task4;

import src.CourseEntry;
import src.Level;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class LevelSummarizer {
    private LevelSummarizer() {
    }

    public static Map<Level, LevelSummary> summarize(List<CourseEntry> courses) {
        return courses.stream()
                .collect(Collectors.groupingBy(
                        CourseEntry::level,
                        () -> new EnumMap<>(Level.class),
                        Collectors.teeing(
                                Collectors.counting(),
                                Collectors.averagingInt(CourseEntry::durationHours),
                                LevelStats::new)))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new LevelSummary(
                                entry.getKey(),
                                entry.getValue().count(),
                                entry.getValue().averageDuration()),
                        (left, right) -> left,
                        () -> new EnumMap<>(Level.class)));
    }
}
