package task7;

public record CourseCompletion(String code, long enrollments, long completions) {
    public double completionRate() {
        return enrollments == 0 ? 0.0 : (double) completions / enrollments;
    }
}
