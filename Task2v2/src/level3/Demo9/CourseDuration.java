package level3.Demo9;

public class CourseDuration {
    private static final int MIN_HOURS = 10;
    private static final int MAX_HOURS = 100;
    private final int hours;

    public CourseDuration(int hours) {
        if (hours < MIN_HOURS || hours > MAX_HOURS) {
            throw new IllegalArgumentException("Duration out of bounds");
        }
        this.hours = hours;
    }

    public int getHours() { return hours; }
}