package level3.Demo10;

public class CourseCode {
    private final String normalizedCode;

    public CourseCode(String rawCode) {
        if (rawCode == null || rawCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Code cannot be empty");
        }
        this.normalizedCode = rawCode.trim().toUpperCase().replace(" ", "-");
        if (!normalizedCode.matches("[A-Z0-9\\-]+")) {
            throw new IllegalArgumentException("Invalid characters in code");
        }
    }

    public String getNormalizedCode() { return normalizedCode; }
}