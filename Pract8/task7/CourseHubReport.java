package task7;

import java.util.List;

public record CourseHubReport(
        long totalEvents,
        long validEvents,
        long invalidEvents,
        int uniqueStudents,
        List<CourseCompletion> completionByCourse,
        List<CourseCompletion> top3,
        List<LevelShare> distributionByLevel,
        List<Anomaly> anomalies,
        boolean anomaliesTruncated) {

    public CourseHubReport {
        completionByCourse = List.copyOf(completionByCourse);
        top3 = List.copyOf(top3);
        distributionByLevel = List.copyOf(distributionByLevel);
        anomalies = List.copyOf(anomalies);
    }
}
