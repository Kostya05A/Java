package task3;

import src.CourseEntry;

import java.util.List;

public final class DurationAggregator {
    private DurationAggregator() {
    }

    public static DurationTotals sumSequential(List<CourseEntry> courses) {
        return sumWithIdentity(courses, DurationTotals.IDENTITY);
    }

    public static DurationTotals sumParallel(List<CourseEntry> courses) {
        return courses.parallelStream()
                .map(DurationTotals::of)
                .reduce(DurationTotals.IDENTITY, DurationTotals::combine, DurationTotals::combine);
    }

    public static DurationTotals sumWithIdentity(List<CourseEntry> courses, DurationTotals identity) {
        return courses.stream()
                .map(DurationTotals::of)
                .reduce(identity, DurationTotals::combine);
    }
}
