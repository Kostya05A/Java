package level5;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import domain.Course;
import domain.CourseCode;
import domain.EnrollmentService;
import domain.ErrorCode;

public class CourseImporter {

    public static final int DEFAULT_CAPACITY = 30;

    private final EnrollmentService.CourseCatalog catalog;

    public CourseImporter(EnrollmentService.CourseCatalog catalog) {
        this.catalog = catalog;
    }

    public int importFrom(Path file) throws SourceUnavailableException, ImportAbortedException {
        List<String> lines = readLines(file);

        Map<CourseCode, Course> stateBefore = catalog.snapshot();

        int recordNumber = 0;
        try {
            for (String line : lines) {
                recordNumber++;
                if (line.isBlank()) {
                    continue;
                }
                catalog.add(parseCourse(recordNumber, line));
            }
        } catch (ImportAbortedException ex) {

            catalog.restore(stateBefore);
            throw ex;
        }
        return recordNumber;
    }

    private List<String> readLines(Path file) throws SourceUnavailableException {
        try {
            return Files.readAllLines(file);
        } catch (MalformedInputException ex) {

            throw new SourceUnavailableException(
                    file, "Файл не в кодировке UTF-8", ErrorCode.MALFORMED_RECORD, ex);
        } catch (IOException ex) {
            throw new SourceUnavailableException(
                    file, "Файл импорта недоступен", ErrorCode.SOURCE_UNAVAILABLE, ex);
        }
    }

    private Course parseCourse(int recordNumber, String rawRecord) throws ImportAbortedException {
        String[] parts = rawRecord.split(";");

        if (parts.length != 3) {
            throw new ImportAbortedException(
                    recordNumber, rawRecord,
                    "ожидалось 3 поля, получено " + parts.length);
        }

        String rawCode = parts[0].trim();
        String title = parts[1].trim();
        String rawHours = parts[2].trim();

        if (!CourseCode.isValid(rawCode)) {
            throw new ImportAbortedException(
                    recordNumber, rawRecord,
                    "код '" + rawCode + "' не соответствует формату [A-Z]{2,4}-<3 цифры>");
        }

        if (title.isEmpty()) {
            throw new ImportAbortedException(recordNumber, rawRecord, "название курса пустое");
        }

        int hours;
        try {
            hours = Integer.parseInt(rawHours);
        } catch (NumberFormatException ex) {

            throw new ImportAbortedException(
                    recordNumber, rawRecord,
                    "часы '" + rawHours + "' не число", ex);
        }

        if (hours < Course.MIN_DURATION_HOURS || hours > Course.MAX_DURATION_HOURS) {
            throw new ImportAbortedException(
                    recordNumber, rawRecord,
                    "часы " + hours + " вне диапазона ["
                            + Course.MIN_DURATION_HOURS + ".." + Course.MAX_DURATION_HOURS + "]");
        }

        Course course = new Course(CourseCode.of(rawCode), title, DEFAULT_CAPACITY);
        course.completeHours(hours);
        return course;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Задание 14. Импорт: два отказа, два разных последствия ===");

        Path good = Files.createTempFile("courses-good", ".csv");
        Files.writeString(good, """
                JAVA-101;Основы Java;40
                WEB-200;Веб-разработка;30
                DB-300;Базы данных;25
                """);

        Path bad = Files.createTempFile("courses-bad", ".csv");
        Files.writeString(bad, """
                JAVA-101;Основы Java;40
                WEB-200;Веб-разработка;30
                NOPE;Мусор;20
                """);

        System.out.println();
        System.out.println("--- 1. Успешный импорт: 3 курса ---");
        EnrollmentService.CourseCatalog catalog = new EnrollmentService.CourseCatalog();
        CourseImporter importer = new CourseImporter(catalog);
        System.out.println("  записей: " + importer.importFrom(good));
        System.out.println("  в каталоге: " + catalog.size());
        for (Course course : catalog.all()) {
            System.out.println("    " + course);
        }

        System.out.println();
        System.out.println("--- 2. Плохая запись на 3-й строке: ИМПОРТ ОТКАТЕН ---");
        System.out.println("  до:    в каталоге " + catalog.size() + " курсов");
        try {
            importer.importFrom(bad);
        } catch (ImportAbortedException ex) {
            System.out.println("  отказ: запись №" + ex.recordNumber() + " - " + ex.message());
            System.out.println("  запись: \"" + ex.rawRecord() + "\"");
            System.out.println("  cause:  " + (ex.getCause() == null ? "нет" : ex.getCause().getClass().getSimpleName()));
        }
        System.out.println("  после: в каталоге " + catalog.size() + " курсов");
        System.out.println("  => первые 2 курса из плохого файла НЕ остались в каталоге");
        System.out.println("  => состояние то же, что до импорта: откат атомарный");

        System.out.println();
        System.out.println("--- 3. Файл недоступен: отказ ДРУГОГО типа, отката не требуется ---");
        try {
            importer.importFrom(Path.of("no-such-file.csv"));
        } catch (SourceUnavailableException ex) {
            System.out.println("  тип:       " + ex.getClass().getSimpleName());
            System.out.println("  errorCode: " + ex.errorCode());
            System.out.println("  причина:   " + ex.getCause().getClass().getSimpleName());
            System.out.println("  в каталоге: " + catalog.size() + " курсов (не изменилось)");
        }

        System.out.println();
        System.out.println("--- 4. Почему это два разных типа, а не одно исключение ---");
        System.out.println("  SourceUnavailableException  -> оператор: поправить доступ и повторить");
        System.out.println("  ImportAbortedException      -> разработчик: починить строку в файле");
        System.out.println("  Если бы это был один тип, вызывающий не смог бы");
        System.out.println("  отличить «данные плохие» от «система сломалась».");

        Files.deleteIfExists(good);
        Files.deleteIfExists(bad);
    }
}
