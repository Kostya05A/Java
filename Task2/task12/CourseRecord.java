public class CourseRecord {
  private final long id;
  private final String title;
  private final int hours;

  public CourseRecord(long id, String title, int hours) {
    this.id = id;
    this.title = title;
    this.hours = hours;
  }

  public long id() {
    return id;
  }

  public String title() {
    return title;
  }

  public int hours() {
    return hours;
  }

  @Override
  public String toString() {
    return "CourseRecord[id=" + id + ", title=" + title + ", hours=" + hours + "]";
  }
}