package level1;

import java.io.IOException;

public class Demo3 {

    public static void main(String[] args) {
        System.out.println("=== Задание 3. EnrollmentRejectedException ===");

        EnrollmentRejectedException noSeats =
                new EnrollmentRejectedException(1001L, 42L, EnrollmentRejectedException.Reason.NO_SEATS);
        show("без cause", noSeats);

        Throwable original = new IOException("connect timed out after 30000 ms");
        EnrollmentRejectedException wrapped =
                new EnrollmentRejectedException(1001L, 42L, EnrollmentRejectedException.Reason.PREREQ_MISSING, original);
        show("с cause", wrapped);

        System.out.println();
        System.out.println("--- цепочка cause ---");
        Throwable current = wrapped;
        int depth = 0;
        while (current != null) {
            System.out.println("  [" + depth + "] " + current.getClass().getName() + ": " + current.getMessage());
            current = current.getCause();
            depth++;
        }
        System.out.println("  original == getCause(): " + (wrapped.getCause() == original));

        System.out.println();
        System.out.println("--- поля ---");
        System.out.println("  studentId = " + wrapped.studentId());
        System.out.println("  courseId  = " + wrapped.courseId());
        System.out.println("  reason    = " + wrapped.reason());

        System.out.println();
        System.out.println("--- что показываем пользователю ---");
        System.out.println("  message:  " + userMessage(wrapped));
        System.out.println("  toString: " + wrapped);
    }

    private static String userMessage(EnrollmentRejectedException ex) {
        return switch (ex.reason()) {
            case NO_SEATS -> "Свободных мест нет. Вы в списке ожидания.";
            case PREREQ_MISSING -> "Сначала пройдите пререквизит курса.";
            case COURSE_CLOSED -> "Курс закрыт для зачисления.";
            case DUPLICATE -> "Вы уже подавали заявку на этот курс.";
            case COURSE_NOT_FOUND -> "Такого курса нет. Проверьте код.";
        };
    }

    private static void show(String title, EnrollmentRejectedException ex) {
        System.out.println();
        System.out.println("--- " + title + " ---");
        System.out.println("  getMessage(): " + ex.getMessage());
        System.out.println("  toString():   " + ex);
    }
}
