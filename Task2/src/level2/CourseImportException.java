package level2;

public class CourseImportException extends Exception {

    private final int recordNumber;
    private final String rawRecord;

    public CourseImportException(int recordNumber, String rawRecord, String message, Throwable cause) {
        super(message, cause);
        this.recordNumber = recordNumber;
        this.rawRecord = rawRecord;
    }

    public int recordNumber() {
        return recordNumber;
    }

    public String rawRecord() {
        return rawRecord;
    }

    @Override
    public String toString() {
        return "CourseImportException[record=" + recordNumber
                + ", cause=" + (getCause() == null ? "none" : getCause().getClass().getSimpleName())
                + "]";
    }
}
