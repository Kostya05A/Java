package level5;

public class ImportAbortedException extends Exception {

    private final int recordNumber;
    private final String rawRecord;
    private final String message;

    public ImportAbortedException(int recordNumber, String rawRecord, String message) {
        super(message, null);
        this.recordNumber = recordNumber;
        this.rawRecord = rawRecord;
        this.message = message;
    }

    public ImportAbortedException(int recordNumber, String rawRecord, String message, Throwable cause) {
        super(message, cause);
        this.recordNumber = recordNumber;
        this.rawRecord = rawRecord;
        this.message = message;
    }

    public int recordNumber() {
        return recordNumber;
    }

    public String rawRecord() {
        return rawRecord;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "ImportAbortedException[record=" + recordNumber
                + ", message=" + message
                + ", hasCause=" + (getCause() != null) + "]";
    }
}
