package task1;

import src.CourseEntry;

import java.util.Comparator;
import java.util.List;

public final class CourseSorter {
    private static final Comparator<CourseEntry> FULL_ORDER =
            Comparator.comparing(CourseEntry::level)
                    .thenComparingInt(CourseEntry::durationHours)
                    .thenComparing(CourseEntry::code)
                    .thenComparing(CourseEntry::title);

    private static final Comparator<CourseEntry> INCOMPLETE_ORDER =
            Comparator.comparing(CourseEntry::level)
                    .thenComparingInt(CourseEntry::durationHours);

    private CourseSorter() {
    }

    public static List<CourseEntry> sorted(List<CourseEntry> courses) {
        return courses.stream().sorted(FULL_ORDER).toList();
    }

    public static List<CourseEntry> sortedWithoutTieBreakers(List<CourseEntry> courses) {
        return courses.stream().sorted(INCOMPLETE_ORDER).toList();
    }

    public static String orderDescription() {
        return "level -> durationHours -> code -> title";
    }
}
