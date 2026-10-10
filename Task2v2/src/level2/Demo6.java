package level2;

public class Demo6 {
    public static void main(String[] args) {
        Importer importer = new Importer();
        try {
            importer.importData("100", "abc");
        } catch (CourseImportException e) {
            System.out.println("Ошибка импорта записи " + e.getRecordNumber());
            System.out.println("Исходная причина: " + e.getCause().getMessage());
        }
    }
}

class CourseImportException extends Exception {
    private final int recordNumber;
    CourseImportException(int recordNumber, Throwable cause) {
        super("Неверный формат данных", cause);
        this.recordNumber = recordNumber;
    }
    int getRecordNumber() { return recordNumber; }
}

class Parser {
    int parse(String value) {
        return Integer.parseInt(value);
    }
}

class Importer {
    private final Parser parser = new Parser();
    void importData(String recordNumStr, String value) throws CourseImportException {
        int recordNum = Integer.parseInt(recordNumStr);
        try {
            parser.parse(value);
        } catch (NumberFormatException e) {
            throw new CourseImportException(recordNum, e);
        }
    }
}
