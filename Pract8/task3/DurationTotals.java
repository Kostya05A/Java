package task3;

import src.CourseEntry;

public record DurationTotals(long totalDuration, long totalCompleted, int courseCount) {
    public static final DurationTotals IDENTITY = new DurationTotals(0, 0, 0);

    public static DurationTotals of(CourseEntry course) {
        return new DurationTotals(course.durationHours(), course.completedHours(), 1);
    }

    public DurationTotals combine(DurationTotals other) {
        return new DurationTotals(
                totalDuration + other.totalDuration,
                totalCompleted + other.totalCompleted,
                courseCount + other.courseCount);
    }
}
