package level2;

public class CourseImportService {

    public int parseHours(int recordNumber, String rawRecord, String hoursText) throws CourseImportException {
        try {

            return RawNumberParser.parseHours(hoursText);
        } catch (NumberFormatException ex) {

            throw new CourseImportException(
                    recordNumber,
                    rawRecord,
                    "Неверный формат часов в записи №" + recordNumber,
                    ex
            );
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Задание 6. Перевод исключения ===");
        CourseImportService service = new CourseImportService();

        try {
            service.parseHours(5, "JAVA-101;Основы Java;сорок", "сорок");
        } catch (CourseImportException ex) {
            System.out.println("  поймали на уровне импорта:");
            System.out.println("    " + ex);
            System.out.println("    recordNumber: " + ex.recordNumber());
            System.out.println("    rawRecord:    " + ex.rawRecord());
            System.out.println("    getMessage(): " + ex.getMessage());
            System.out.println("    cause:        " + ex.getCause().getClass().getSimpleName()
                    + ": " + ex.getCause().getMessage());
        }
    }
}
