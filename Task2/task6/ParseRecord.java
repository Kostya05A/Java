import src.Course;

public class ParseRecord {
  public static Course parseRecord(String line, int recordNumber) 
    throws CourseImportException {
      try {
        int duration = ParseDuration.parseDuration(line);
        return new Course(recordNumber, "Imported", duration);
      } catch (NumberFormatException e) {
        throw new CourseImportException(
          recordNumber,
          "Invalid duration in record " + recordNumber,
          e
        );
      }
    }
}
