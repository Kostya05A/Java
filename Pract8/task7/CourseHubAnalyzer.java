package task7;

import src.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

public final class CourseHubAnalyzer {
    public static final int MAX_ANOMALIES = 10;

    private CourseHubAnalyzer() {
    }

    public static CourseHubReport process(Stream<EnrollmentEvent> events) {
        long totalEvents = 0;
        long validEvents = 0;
        long invalidEvents = 0;
        Set<String> students = new HashSet<>();
        Map<String, CourseStats> statsByCourse = new TreeMap<>();
        EnumMap<Level, Long> eventsByLevel = new EnumMap<>(Level.class);
        List<Anomaly> anomalies = new ArrayList<>();
        boolean anomaliesTruncated = false;

        Iterator<EnrollmentEvent> iterator = events.iterator();
        while (iterator.hasNext()) {
            EnrollmentEvent event = iterator.next();
            totalEvents++;

            String reason = validate(event);
            if (reason != null) {
                invalidEvents++;
                if (anomalies.size() < MAX_ANOMALIES) {
                    anomalies.add(new Anomaly(
                            totalEvents,
                            event == null ? "" : safe(event.studentId()),
                            event == null ? "" : safe(event.courseCode()),
                            reason));
                } else {
                    anomaliesTruncated = true;
                }
                continue;
            }

            validEvents++;
            students.add(event.studentId().trim());
            statsByCourse.computeIfAbsent(
                    event.courseCode().trim(),
                    code -> new CourseStats()).accept(event.completed());
            eventsByLevel.merge(event.level(), 1L, Long::sum);
        }

        List<CourseCompletion> completionByCourse = statsByCourse.entrySet().stream()
                .map(entry -> new CourseCompletion(
                        entry.getKey(),
                        entry.getValue().enrollments(),
                        entry.getValue().completions()))
                .toList();

        List<CourseCompletion> top3 = completionByCourse.stream()
                .sorted(Comparator
                        .comparingDouble(CourseCompletion::completionRate)
                        .reversed()
                        .thenComparing(Comparator
                                .comparingLong(CourseCompletion::enrollments)
                                .reversed())
                        .thenComparing(CourseCompletion::code))
                .limit(3)
                .toList();

        List<LevelShare> distributionByLevel = new ArrayList<>();
        for (Level level : Level.values()) {
            long enrollments = eventsByLevel.getOrDefault(level, 0L);
            double share = validEvents == 0 ? 0.0 : (double) enrollments / validEvents;
            distributionByLevel.add(new LevelShare(level, enrollments, share));
        }

        return new CourseHubReport(
                totalEvents,
                validEvents,
                invalidEvents,
                students.size(),
                completionByCourse,
                top3,
                distributionByLevel,
                anomalies,
                anomaliesTruncated);
    }

    private static String validate(EnrollmentEvent event) {
        if (event == null) {
            return "событие отсутствует";
        }
        if (event.studentId() == null || event.studentId().isBlank()) {
            return "пустой studentId";
        }
        if (event.courseCode() == null || event.courseCode().isBlank()) {
            return "пустой courseCode";
        }
        if (event.level() == null) {
            return "не задан level";
        }
        if (event.durationHours() < 1 || event.durationHours() > 300) {
            return "duration вне диапазона 1..300";
        }
        return null;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static final class CourseStats {
        private long enrollments;
        private long completions;

        private void accept(boolean completed) {
            enrollments++;
            if (completed) {
                completions++;
            }
        }

        private long enrollments() {
            return enrollments;
        }

        private long completions() {
            return completions;
        }
    }
}
