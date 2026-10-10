public class Demo3 {
    public static void main(String[] args) {
        try {
            enrollStudent("S1", "C1", "PREREQ_FAIL");
        } catch (EnrollmentRejectedException e) {
            System.out.println("Перехвачено: " + e.getMessage());
            System.out.println("Код причины: " + e.getReasonCode());
            System.out.println("Исходная причина: " + e.getCause());
        }
    }

    static void enrollStudent(String studentId, String courseId, String reason) {
        try {
            throw new RuntimeException("Внутренняя ошибка проверки");
        } catch (RuntimeException e) {
            throw new EnrollmentRejectedException(studentId, courseId, reason, e);
        }
    }
}

class EnrollmentRejectedException extends RuntimeException {
    private final String studentId;
    private final String courseId;
    private final String reasonCode;

    public EnrollmentRejectedException(String studentId, String courseId, String reasonCode, Throwable cause) {
        super("Зачисление отклонено");
        this.studentId = studentId;
        this.courseId = courseId;
        this.reasonCode = reasonCode;
        initCause(cause);
    }

    public String getReasonCode() { return reasonCode; }

    @Override
    public String toString() {
        return "EnrollmentRejectedException{reason=" + reasonCode + "}";
    }
}