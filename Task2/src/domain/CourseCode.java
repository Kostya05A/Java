package domain;

public final class CourseCode implements Comparable<CourseCode> {

    private final String value;

    private CourseCode(String value) {
        this.value = value;
    }

    public static CourseCode of(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("код курса не может быть null");
        }
        String normalized = raw.trim()
                .toUpperCase()
                .replace(' ', '-')
                .replace('_', '-');

        if (!normalized.matches("[A-Z]{2,4}-\\d{3}")) {
            throw new IllegalArgumentException(
                    "код курса '" + raw + "' не соответствует формату [A-Z]{2,4}-<3 цифры>");
        }
        return new CourseCode(normalized);
    }

    public static boolean isValid(String raw) {
        try {
            of(raw);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public String value() {
        return value;
    }

    @Override
    public int compareTo(CourseCode other) {
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CourseCode code)) {
            return false;
        }
        return value.equals(code.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
