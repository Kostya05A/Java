package level3.Demo8;

public class Course {
    private int completedHours;
    private final int requiredHours;

    public Course(int requiredHours) {
        this.requiredHours = requiredHours;
        this.completedHours = 0;
    }

    public void completeHours(int hours) {
        if (hours < 0) throw new IllegalArgumentException("Hours cannot be negative");
        this.completedHours += hours;
    }

    public int getCompletedHours() { return completedHours; }
    public boolean isFinished() { return completedHours >= requiredHours; }
}