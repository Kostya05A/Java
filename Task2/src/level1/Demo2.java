package level1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import domain.Course;

public class Demo2 {

    public static void main(String[] args) {
        situation1();
        situation2();
        situation3();
        situation4();
        situation5();
        situation6();
        situation7();
        situation8();
        situation9();
        situation10();
    }

    static void situation1() {
        header("1. Неверный аргумент метода", "unchecked -> IllegalArgumentException");
        try {
            sendNotification(null);
        } catch (IllegalArgumentException ex) {
            System.out.println("   поймали: " + ex.getMessage());
        }
        System.out.println("   сигнатура sendNotification не содержит throws - компилятор не заставил ловить");
    }

    static void sendNotification(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email не может быть пустым");
        }
        System.out.println("   уведомление отправлено на " + email);
    }

    static void situation2() {
        header("2. Отсутствующий файл", "checked -> IOException");
        try {
            readConfig("settings.conf");
        } catch (IOException ex) {
            System.out.println("   поймали: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            System.out.println("   решение вызывающего: создать файл по умолчанию и продолжить работу");
        }
    }

    static String readConfig(String path) throws IOException {
        throw new FileNotFoundException(path);
    }

    static void situation3() {
        header("3. Нарушение состояния", "unchecked -> IllegalStateException");
        Course course = new Course("JAVA-101", false);
        try {
            course.enroll();
        } catch (IllegalStateException ex) {
            System.out.println("   поймали: " + ex.getMessage());
        }
        System.out.println("   это баг в коде (курс закрыт), а не внешний отказ");
    }

    static final class Course {
        private final String code;
        private final boolean open;

        Course(String code, boolean open) {
            this.code = code;
            this.open = open;
        }

        void enroll() {
            if (!open) {
                throw new IllegalStateException("курс " + code + " закрыт");
            }
            System.out.println("   зачисление в " + code);
        }
    }

    static void situation4() {
        header("4. Сетевой отказ", "checked -> IOException");
        try {
            fetch("https://api.example.com/courses");
        } catch (IOException ex) {
            System.out.println("   поймали: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            System.out.println("   решение вызывающего: retry с backoff, потом сообщить пользователю");
        }
    }

    static String fetch(String url) throws IOException {
        throw new IOException("503 Service Unavailable: " + url);
    }

    static void situation5() {
        header("5. Ошибка конфигурации", "другой результат -> fail-fast при старте");
        try {
            startApplication();
        } catch (RuntimeException ex) {
            System.out.println("   поймали при старте: " + ex.getMessage());
            System.out.println("   процесс завершается, данные не портятся");
        }
    }

    static void startApplication() {
        String apiKey = System.getenv("COURSEHUB_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("COURSEHUB_API_KEY не задан - конфигурация некорректна");
        }
        System.out.println("   приложение запущено");
    }

    static void situation6() {
        header("6. Недостаток памяти", "невосстановимо -> OutOfMemoryError");
        System.out.println("   доступно куч: " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " МБ");
        try {

            long impossible = (long) Runtime.getRuntime().maxMemory() * 4;
            if (impossible > Integer.MAX_VALUE - 8) {
                impossible = Integer.MAX_VALUE - 8;
            }
            byte[] block = new byte[(int) impossible];
            System.out.println("   памяти хватило (бывает и так): " + block.length);
        } catch (OutOfMemoryError error) {
            System.out.println("   поймали: " + error.getClass().getSimpleName());
            System.out.println("   продолжать работу нельзя; корректное завершение: закрыть ресурсы и выйти");
        }
    }

    static void situation7() {
        header("7. Курс не найден", "другой результат -> Optional.empty()");
        Optional<String> found = findCourse("NOPE-999");
        System.out.println("   findCourse вернул: " + found);
        if (found.isEmpty()) {
            System.out.println("   это штатный исход, а не поломка");
        }
    }

    static Optional<String> findCourse(String code) {
        if (code.equals("JAVA-101")) {
            return Optional.of("Основы Java");
        }
        return Optional.empty();
    }

    static void situation8() {
        header("8. Запись импорта не соответствует формату", "другой результат -> запись в отчёте");
        ImportReport report = new ImportReport();
        for (String line : new String[]{"JAVA-101;Основы Java;40", "WEB-200;;abc", "DB-300;Базы данных;30"}) {
            String[] parts = line.split(";");
            if (parts.length != 3 || parts[1].isBlank()) {
                report.addError(line, "ожидалось 3 поля, код и название не пустые");
                System.out.println("   строка битая, импорт продолжается: " + line);
            } else {
                report.addOk(line);
                System.out.println("   импортировано: " + parts[0]);
            }
        }
        System.out.println("   отчёт: успешно=" + report.okCount() + ", ошибок=" + report.errorCount());
    }

    static final class ImportReport {
        private int ok;
        private final List<String> errors = new ArrayList<>();

        void addOk(String line) {
            ok++;
        }

        void addError(String line, String reason) {
            errors.add(line + " -> " + reason);
        }

        int okCount() {
            return ok;
        }

        int errorCount() {
            return errors.size();
        }
    }

    static void situation9() {
        header("9. Отказ платёжного шлюза", "другой результат -> типизированный PaymentResult");
        PaymentResult result = charge("4242424242424242", 4900);
        System.out.println("   результат: " + result.status() + " (" + result.status() + " != FAILED, значит исключения не было)");
        System.out.println("   клиент может повторить с другим способом оплаты - это не авария");
    }

    record PaymentResult(PaymentStatus status, String reference) {}

    enum PaymentStatus {
        PAID, DECLINED
    }

    static PaymentResult charge(String card, long amount) {
        if (amount > 4000) {
            return new PaymentResult(PaymentStatus.DECLINED, "limit-exceeded");
        }
        return new PaymentResult(PaymentStatus.PAID, "pay-001");
    }

    static void situation10() {
        header("10. Внутренняя ошибка разработчика", "невосстановимо -> не ловим, чиним код");
        try {
            brokenInternalCall();
        } catch (RuntimeException ex) {
            System.out.println("   поймали (в реальном коде так ловить не надо): " + ex.getClass().getSimpleName());
            System.out.println("   правильное действие - исправить brokenInternalCall, а не добавить catch");
        }
    }

    static void brokenInternalCall() {

        int[] seats = new int[10];
        System.out.println("   мест осталось: " + seats[42]);
    }

    private static void header(String title, String decision) {
        System.out.println();
        System.out.println("=== " + title + " -> " + decision + " ===");
    }
}
