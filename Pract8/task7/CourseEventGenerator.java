package task7;

import src.Level;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class CourseEventGenerator {
    public static final int EVENT_COUNT = 500_000;

    private CourseEventGenerator() {
    }

    public static Stream<EnrollmentEvent> generate(List<CatalogEntry> catalog) {
        return IntStream.range(0, EVENT_COUNT).mapToObj(index -> {
            int special = index % 25_000;
            if (special == 7) {
                return new EnrollmentEvent("S-bad-" + index, " ", Level.BEGINNER, 10, false);
            }
            if (special == 13) {
                return new EnrollmentEvent(" ", "JAVA101", Level.BEGINNER, 10, false);
            }
            if (special == 19) {
                return new EnrollmentEvent("S-bad-" + index, "JAVA101", null, 10, false);
            }
            if (special == 23) {
                return new EnrollmentEvent("S-bad-" + index, "JAVA101", Level.BEGINNER, 0, false);
            }

            CatalogEntry course = catalog.get(index % catalog.size());
            String studentId = "S-" + (index % 5_000);
            int cycle = index / catalog.size();
            boolean completed = Math.floorMod(cycle * 31 + course.code().hashCode(), 100)
                    < course.completionThreshold();
            return new EnrollmentEvent(
                    studentId,
                    course.code(),
                    course.level(),
                    course.durationHours(),
                    completed);
        });
    }
}
