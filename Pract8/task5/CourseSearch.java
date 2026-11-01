package task5;

import src.CourseEntry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class CourseSearch {
    private static final Comparator<CourseEntry> LONGEST_ORDER =
            Comparator.comparingInt(CourseEntry::durationHours)
                    .thenComparing(CourseEntry::code);

    private CourseSearch() {
    }

    public static Optional<CourseEntry> longestCourse(List<CourseEntry> courses) {
        return courses.stream().max(LONGEST_ORDER);
    }
}
