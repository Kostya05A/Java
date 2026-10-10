package level5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import domain.Course;
import domain.CourseCode;
import domain.EnrollmentResult;
import domain.EnrollmentService;
import domain.ErrorCode;

public final class CourseHub {

    private final EnrollmentService.CourseCatalog catalog;
    private final EnrollmentService enrollment;
    private final CourseImporter importer;

    public CourseHub() {
        this.catalog = new EnrollmentService.CourseCatalog();
        this.enrollment = new EnrollmentService(catalog, new InMemoryTranscripts());
        this.importer = new CourseImporter(catalog);
    }

    public OperationResult enroll(long studentId, String rawCourseCode) {
        CourseCode code;
        try {
            code = CourseCode.of(rawCourseCode);
        } catch (IllegalArgumentException ex) {
            return OperationResult.failure(ErrorCode.COURSE_NOT_FOUND,
                    "Код курса не распознан: " + rawCourseCode);
        }

        EnrollmentResult result = enrollment.enroll(studentId, code);

        if (result.isAccepted()) {
            return OperationResult.success("Студент " + studentId + " зачислен в " + code);
        }
        if (result.status() == EnrollmentResult.Status.WAITLISTED) {
            return OperationResult.success(ErrorCode.NO_SEATS,
                    "Свободных мест нет, " + studentId + " в листе ожидания курса " + code);
        }
        return OperationResult.failure(result.reason(),
                "Зачисление невозможно: " + result.reason().clientMessage());
    }

    public Optional<Course> findCourse(String rawCourseCode) {
        if (!CourseCode.isValid(rawCourseCode)) {
            return Optional.empty();
        }
        return Optional.ofNullable(catalog.find(CourseCode.of(rawCourseCode)));
    }

    public OperationResult importCourses(Path file) {
        try {
            int count = importer.importFrom(file);
            return OperationResult.success("Загружено курсов: " + count);
        } catch (ImportAbortedException ex) {
            logInternal("IMPORT_ABORTED", ex);
            return OperationResult.failure(ErrorCode.MALFORMED_RECORD,
                    "Импорт прерван: запись №" + ex.recordNumber() + " не соответствует формату");
        } catch (SourceUnavailableException ex) {
            logInternal("SOURCE_UNAVAILABLE", ex);
            return OperationResult.failure(ex.errorCode(),
                    "Файл импорта недоступен. Повторите попытку позже.");
        }
    }

    public OperationResult cancelEnrollment(long studentId, String rawCourseCode) {
        CourseCode code;
        try {
            code = CourseCode.of(rawCourseCode);
        } catch (IllegalArgumentException ex) {
            return OperationResult.failure(ErrorCode.MALFORMED_RECORD,
                    "Некорректный код курса: " + rawCourseCode);
        }

        Course course = catalog.find(code);
        if (course == null) {
            return OperationResult.failure(ErrorCode.COURSE_NOT_FOUND, "Курс не найден");
        }

        course.removeStudent(studentId);
        return OperationResult.success("Студент " + studentId + " отчислен из " + code);
    }

    public int courseCount() {
        return catalog.size();
    }

    private static void logInternal(String event, Throwable detail) {
        System.err.println("[CourseHub] " + event + ": " + detail);
    }

    private static final class InMemoryTranscripts implements EnrollmentService.TranscriptRepository {
        @Override
        public boolean hasCompleted(long studentId, CourseCode courseCode) {
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Задание 15. Публичный слой CourseHub ===");
        CourseHub hub = new CourseHub();

        System.out.println();
        System.out.println("--- 1. Импорт каталога ---");
        Path good = Path.of("hub-good.csv");
        writeFile(good, "JAVA-101;Основы Java;40\nWEB-200;Веб;30\n");
        print(hub.importCourses(good));
        deleteFile(good);

        System.out.println();
        System.out.println("--- 2. Зачисление: 0 исключений наружу, только коды ---");
        print(hub.enroll(1, "java 101"));
        print(hub.enroll(1, "JAVA-101"));
        print(hub.enroll(2, "NOPE-999"));
        print(hub.enroll(2, "плохой код"));
        print(hub.enroll(2, "WEB-200"));
        print(hub.enroll(2, "DB-300"));

        System.out.println();
        System.out.println("--- 3. Лист ожидания: успех, но не зачисление ---");
        for (int i = 10; i <= 40; i++) {
            hub.enroll(i, "WEB-200");
        }
        print(hub.enroll(100, "WEB-200"));

        System.out.println();
        System.out.println("--- 4. Импорт: недоступный файл -> SOURCE_UNAVAILABLE ---");
        print(hub.importCourses(Path.of("нет-такого.csv")));

        System.out.println();
        System.out.println("--- 5. Импорт: плохая запись -> MALFORMED_RECORD + откат ---");
        Path bad = Path.of("hub-bad.csv");
        writeFile(bad, "NOPE;Мусор;20\n");
        int before = hub.courseCount();
        print(hub.importCourses(bad));
        System.out.println("  курсов в каталоге: " + before + " -> " + hub.courseCount());
        deleteFile(bad);

        System.out.println();
        System.out.println("--- 6. Отмена зачисления ---");
        print(hub.cancelEnrollment(1, "JAVA-101"));
        print(hub.cancelEnrollment(1, "NOPE-999"));

        System.out.println();
        System.out.println("--- 7. findCourse: отсутствие - не ошибка ---");
        System.out.println("  findCourse(\"JAVA-101\") = " + hub.findCourse("JAVA-101").isPresent());
        System.out.println("  findCourse(\"NOPE-999\") = " + hub.findCourse("NOPE-999").isPresent());
        System.out.println("  findCourse(\"мусор\")    = " + hub.findCourse("мусор").isPresent());
    }

    private static void writeFile(Path file, String content) {
        try {
            Files.writeString(file, content);
        } catch (IOException ex) {
            throw new IllegalStateException("демонстрация не смогла создать файл " + file, ex);
        }
    }

    private static void deleteFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            throw new IllegalStateException("демонстрация не смогла удалить файл " + file, ex);
        }
    }

    private static void print(OperationResult result) {
        System.out.println("  " + result);
    }
}
