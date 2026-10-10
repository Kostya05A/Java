package level2;

import java.util.Optional;

import domain.Course;
import domain.CourseCode;
import domain.EnrollmentResult;
import domain.ErrorCode;

public class CourseService {

    public Optional<Course> findCourse(String rawCode) {
        Course course = switch (rawCode == null ? "" : rawCode.trim().toUpperCase()) {
            case "JAVA-101" -> Course.of("JAVA-101", "Основы Java", 30);
            case "WEB-200" -> Course.of("WEB-200", "Веб-разработка", 25);
            default -> null;
        };
        return Optional.ofNullable(course);
    }

    public ParseResult parseCourse(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            return ParseResult.failure(ParseError.MALFORMED_FORMAT, "ожидалось 3 поля, получено " + parts.length);
        }
        String rawCode = parts[0];
        String title = parts[1];
        String rawHours = parts[2];

        if (title.isBlank()) {
            return ParseResult.failure(ParseError.EMPTY_TITLE, "название курса пустое");
        }
        if (!CourseCode.isValid(rawCode)) {
            return ParseResult.failure(ParseError.INVALID_CODE, "код '" + rawCode + "' не соответствует формату");
        }
        int hours;
        try {
            hours = Integer.parseInt(rawHours.trim());
        } catch (NumberFormatException ex) {

            return ParseResult.failure(ParseError.INVALID_HOURS, "часы '" + rawHours + "' не число");
        }
        if (hours < Course.MIN_DURATION_HOURS || hours > Course.MAX_DURATION_HOURS) {
            return ParseResult.failure(
                    ParseError.HOURS_OUT_OF_RANGE,
                    "часы " + hours + " вне диапазона [" + Course.MIN_DURATION_HOURS + ".." + Course.MAX_DURATION_HOURS + "]");
        }
        return ParseResult.success(new CourseDraft(CourseCode.of(rawCode), title, hours));
    }

    public EnrollmentResult enroll(long studentId, CourseCode courseCode) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("studentId должен быть положительным");
        }
        return switch (courseCode.value()) {
            case "JAVA-101" -> EnrollmentResult.accepted(studentId, courseCode);
            case "JAVA-102" -> EnrollmentResult.rejected(studentId, courseCode, ErrorCode.COURSE_CLOSED);
            case "JAVA-103" -> EnrollmentResult.waitlisted(studentId, courseCode);
            default -> EnrollmentResult.rejected(studentId, courseCode, ErrorCode.COURSE_NOT_FOUND);
        };
    }

    public long calculatePrice(long studentId, int hours) throws PaymentGatewayException {
        if (hours <= 0) {

            throw new IllegalArgumentException("количество часов должно быть положительным");
        }
        if (studentId == 7777L) {

            throw new PaymentGatewayException("платёжный шлюз недоступен, student=" + studentId);
        }
        return hours * 500L;
    }

    public static void main(String[] args) {
        CourseService service = new CourseService();

        System.out.println("=== Задание 7. Ошибка как значение или исключение ===");
        System.out.println();
        System.out.println("--- КОНТРАСТНЫЙ СЛУЧАЙ A: findCourse (Optional) ---");
        Optional<Course> found = service.findCourse(" java-101 ");
        System.out.println("  findCourse(\" java-101 \") = " + found);
        Optional<Course> missing = service.findCourse("NOPE-999");
        System.out.println("  findCourse(\"NOPE-999\") = " + missing);
        System.out.println("  пустота - это значение, не исключение");
        System.out.println("  граница: вызывающий обязан разобрать пустоту ->");
        String title = missing.map(Course::title).orElse("курс не найден");
        System.out.println("  orElse(\"...\") = " + title);

        System.out.println();
        System.out.println("--- КОНТРАСТНЫЙ СЛУЧАЙ B: parseCourse (типизированный result) ---");
        String[] lines = {
                "JAVA-101;Основы Java;40",
                "WEB-200;Веб-разработка;abc",
                "BADCODE;Что-то;20",
                "JAVA-102;;20",
                "JAVA-103;Курс;999"
        };
        for (int i = 0; i < lines.length; i++) {
            ParseResult result = service.parseCourse(lines[i]);
            if (result.isSuccess()) {
                System.out.println("  [" + (i + 1) + "] OK   " + result.draft());
            } else {
                System.out.println("  [" + (i + 1) + "] FAIL " + result.error()
                        + " -> " + result.message());
            }
        }
        System.out.println("  причина отказа - часть значения (ParseError), не текст исключения");

        System.out.println();
        System.out.println("--- enroll: доменный отказ = значение ---");
        System.out.println("  " + service.enroll(1, CourseCode.of("JAVA-101")));
        System.out.println("  " + service.enroll(1, CourseCode.of("JAVA-102")) + "   (курс закрыт)");
        System.out.println("  " + service.enroll(1, CourseCode.of("JAVA-103")) + "   (мест нет)");
        System.out.println("  " + service.enroll(1, CourseCode.of("NOPE-999")) + "   (курса нет)");

        System.out.println();
        System.out.println("--- calculatePrice: внешний отказ = checked-исключение ---");
        try {
            System.out.println("  цена: " + service.calculatePrice(1, 40));
        } catch (PaymentGatewayException ex) {
            System.out.println("  шлюз упал: " + ex.getMessage());
        }
        try {
            service.calculatePrice(7777L, 40);
        } catch (PaymentGatewayException ex) {
            System.out.println("  шлюз упал: " + ex.getMessage());
        }
        try {
            service.calculatePrice(1, -5);
        } catch (PaymentGatewayException ex) {
            System.out.println("  НЕОЖИДАННО поймано как внешний отказ");
        } catch (IllegalArgumentException ex) {
            System.out.println("  unchecked пролетел мимо catch: " + ex.getMessage());
            System.out.println("  => ошибка вызывающего и отказ внешнего мира - разные вещи");
        }
    }

    public record ParseResult(CourseDraft draft, ParseError error, String message) {

        public static ParseResult success(CourseDraft draft) {
            return new ParseResult(draft, null, null);
        }

        public static ParseResult failure(ParseError error, String message) {
            return new ParseResult(null, error, message);
        }

        public boolean isSuccess() {
            return draft != null;
        }
    }

    public enum ParseError {
        MALFORMED_FORMAT,
        INVALID_CODE,
        EMPTY_TITLE,
        INVALID_HOURS,
        HOURS_OUT_OF_RANGE
    }

    public record CourseDraft(CourseCode code, String title, int hours) {
        @Override
        public String toString() {
            return "CourseDraft[" + code + " '" + title + "' " + hours + " ч]";
        }
    }
}
