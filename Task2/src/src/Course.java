package src;

public class Course {
  public static final int MIN_DURATION_HOURS = 1;
  public static final int MAX_DURATION_HOURS = 100;

  public enum Status { OPEN, CLOSED }

  private long id;
  private String title;
  private int durationHours;
  private int completedHours;

  private Status status = Status.OPEN;
  private int capacity = 30;
  private boolean hasPrerequisite;
  private java.util.LinkedHashSet<Long> enrolled = new java.util.LinkedHashSet<>();

  public Course(long id, String title, int durationHours, int completedHours) {
    if (id <= 0) throw new IllegalArgumentException("id must be > 0");
    this.id = id;

    if (title == null || title.isBlank()) throw new IllegalArgumentException("title must not be blank");
    this.title = title.trim();

    checkDuration(durationHours);
    this.durationHours = durationHours;

    if (completedHours < 0 || completedHours > durationHours) {
      throw new IllegalArgumentException("completedHours must be in [0, " + durationHours + "]");
    }
    this.completedHours = completedHours;
  }

  public Course(long id, String title, int durationHours) {
    this(id, title, durationHours, 0);
  }

  private static void checkDuration(int durationHours) {
    if (durationHours < MIN_DURATION_HOURS || durationHours > MAX_DURATION_HOURS) {
      throw new IllegalArgumentException("durationHours must be in ["
          + MIN_DURATION_HOURS + ", " + MAX_DURATION_HOURS + "]");
    }
  }

  public long id() { return id; }
  public String title() { return title; }
  public int durationHours() { return durationHours; }
  public int completedHours() { return completedHours; }
  public int remainingHours() { return durationHours - completedHours; }
  public boolean isCompleted() { return remainingHours() == 0; }

  public void completeHours(int hours) {
    if (hours <= 0) throw new IllegalArgumentException("hours must be > 0");
    if (remainingHours() < hours) throw new IllegalStateException(
        "cannot complete " + hours + " h, only " + remainingHours() + " h left");
    this.completedHours += hours;
  }

  // для таблицы решений
  public void setStatus(Status status) { this.status = status; }
  public Status status() { return status; }
  public void setCapacity(int capacity) { this.capacity = capacity; }
  public int capacity() { return capacity; }
  public void setHasPrerequisite(boolean has) { this.hasPrerequisite = has; }
  public boolean hasPrerequisite() { return hasPrerequisite; }
  public java.util.Set<Long> enrolledStudents() { return java.util.Collections.unmodifiableSet(enrolled); }
  public void addEnrolled(long studentId) { enrolled.add(studentId); }
  public int enrolledCount() { return enrolled.size(); }
  public int seatsLeft() { return capacity - enrolledCount(); }
}