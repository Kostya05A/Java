package level2;

import java.io.IOException;
import java.util.List;

public class Demo5 {

    public static void main(String[] args) {
        System.out.println("=== Задание 5. Try-with-resources ===");
        System.out.println();

        System.out.println("--- 1. Успех: ресурс закрывается после блока try ---");
        caseSuccess();

        System.out.println();
        System.out.println("--- 2. Исключение в теле try: ресурс всё равно закрывается ---");
        caseBodyThrows();

        System.out.println();
        System.out.println("--- 3. Ошибка и в работе, и в close(): есть suppressed ---");
        caseBothFail();

        System.out.println();
        System.out.println("--- 4. Ресурс закрывается ДО обработки исключения (finally-семантика) ---");
        caseCloseBeforePropagation();

        System.out.println();
        System.out.println("--- 5. Несколько ресурсов: закрываются в обратном порядке ---");
        caseTwoResources();

        System.out.println();
        System.out.println("--- 6. Реализация AutoCloseable не обязана быть AutoCloseable-обёрткой ---");
        caseEffectivelyFinalResource();
    }

    private static void caseSuccess() {
        StringBuilder log = new StringBuilder();
        try {
            try (LoggableResource resource = new LoggableResource("A", log)) {
                resource.use("читаем");
                resource.use("считаем");
            }
        } catch (Exception ex) {

            System.out.println("    НЕОЖИДАННО: " + ex.getMessage());
        }
        printLog(log);
    }

    private static void caseBodyThrows() {
        StringBuilder log = new StringBuilder();
        try {
            try (LoggableResource resource = new LoggableResource("B", log)) {
                resource.use("читаем");
                throw new IOException("сбой чтения данных");
            }
        } catch (Exception ex) {
            System.out.println("    поймали снаружи: " + ex.getClass().getSimpleName()
                    + ": " + ex.getMessage());
            System.out.println("    suppressed у этого исключения: " + List.of(ex.getSuppressed()));
        }
        printLog(log);
    }

    private static void caseBothFail() {
        StringBuilder log = new StringBuilder();
        try {
            try (LoggableResource resource = new LoggableResource("C", log, true)) {
                resource.use("пишем");
                throw new IOException("сбой записи данных");
            }
        } catch (Exception ex) {
            System.out.println("    ОСНОВНОЕ исключение: " + ex.getClass().getSimpleName()
                    + ": " + ex.getMessage());
            System.out.println("    SUPPRESSED (" + ex.getSuppressed().length + "):");
            for (Throwable suppressed : ex.getSuppressed()) {
                System.out.println("      - " + suppressed.getClass().getSimpleName()
                        + ": " + suppressed.getMessage());
            }
            System.out.println("    => основная ошибка видна сразу, ошибка закрытия не потеряна,");
            System.out.println("       но и не перекрывает основную");
        }
        printLog(log);
    }

    private static void caseCloseBeforePropagation() {
        StringBuilder log = new StringBuilder();
        try {
            try (LoggableResource resource = new LoggableResource("D", log, true)) {
                resource.use("работа");
                throw new RuntimeException("основная ошибка");
            }
        } catch (Exception ex) {
            System.out.println("    порядок событий:");
            System.out.println(log.toString().indent(6).stripTrailing());
            System.out.println("    catch выполнился ПОСЛЕ close (см. лог выше)");
            System.out.println("    основное:   " + ex.getClass().getSimpleName()
                    + ": " + ex.getMessage());
            System.out.println("    suppressed: " + ex.getSuppressed()[0].getMessage());
        }
    }

    private static void caseTwoResources() {
        StringBuilder log = new StringBuilder();
        try {
            try (LoggableResource first = new LoggableResource("Первый", log);
                 LoggableResource second = new LoggableResource("Второй", log)) {
                first.use("init");
                second.use("work");
            }
        } catch (Exception ex) {
            System.out.println("    НЕОЖИДАННО: " + ex.getMessage());
        }
        printLog(log);
        System.out.println("    порядок открытия: Первый, Второй");
        System.out.println("    порядок закрытия: Второй, Первый");
    }

    private static void caseEffectivelyFinalResource() {
        StringBuilder log = new StringBuilder();
        LoggableResource resource = new LoggableResource("E", log);
        try {
            try (resource) {
                resource.use("полезная работа");
            }
        } catch (Exception ex) {
            System.out.println("    НЕОЖИДАННО: " + ex.getMessage());
        }
        printLog(log);
    }

    private static void printLog(StringBuilder log) {
        System.out.println("    лог ресурса:");
        System.out.println(log.toString().indent(6).stripTrailing());
    }
}
