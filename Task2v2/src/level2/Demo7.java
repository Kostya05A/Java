package level2;

import java.util.Optional;

public class Demo7 {
    public static void main(String[] args) {
        CourseHub hub = new CourseHub();
        
        Optional<Course> found = hub.findCourse("C1");
        System.out.println("Найден: " + found.isPresent());

        try {
            hub.parseCourse("invalid_data");
        } catch (ParseException e) {
            System.out.println("Ошибка парсинга: " + e.getMessage());
        }

        try {
            hub.enroll("S1", "C1");
        } catch (EnrollmentRejectedException e) {
            System.out.println("Отказ: " + e.getMessage());
        }

        Result<Integer, String> price = hub.calculatePrice("C1", 5);
        if (price.isSuccess()) {
            System.out.println("Цена: " + price.getValue());
        } else {
            System.out.println("Ошибка цены: " + price.getError());
        }
    }
}

class Course { String id; Course(String id) { this.id = id; } }

class CourseHub {
    Optional<Course> findCourse(String id) {
        if ("C1".equals(id)) return Optional.of(new Course(id));
        return Optional.empty();
    }

    Course parseCourse(String data) throws ParseException {
        if (data.contains("invalid")) throw new ParseException("Bad format");
        return new Course("parsed");
    }

    void enroll(String studentId, String courseId) {
        throw new EnrollmentRejectedException("No seats");
    }

    Result<Integer, String> calculatePrice(String courseId, int hours) {
        if (hours < 0) return Result.error("Hours cannot be negative");
        return Result.success(hours * 100);
    }
}

class ParseException extends Exception { ParseException(String msg) { super(msg); } }
class EnrollmentRejectedException extends RuntimeException { EnrollmentRejectedException(String msg) { super(msg); } }

class Result<V, E> {
    private final V value; private final E error; private final boolean success;
    private Result(V v, E e, boolean s) { value = v; error = e; success = s; }
    static <V, E> Result<V, E> success(V v) { return new Result<>(v, null, true); }
    static <V, E> Result<V, E> error(E e) { return new Result<>(null, e, false); }
    boolean isSuccess() { return success; }
    V getValue() { return value; }
    E getError() { return error; }
}
