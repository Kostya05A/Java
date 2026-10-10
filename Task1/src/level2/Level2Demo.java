package src.level2;

import src.level1.NotificationSender;
import src.level1.EmailSender;

public class Level2Demo {
    public static void main(String[] args) {
        System.out.println("=== Задание 4. Абстрактный базовый класс ===");

        AbstractNotificationSender emailSender = new ConcreteEmailSender();
        emailSender.send("Hello", "user@example.com");
        System.out.println();

        AbstractNotificationSender consoleSender = new ConcreteConsoleSender();
        consoleSender.send("Test message", "admin");
        System.out.println();

        System.out.println("Задание 5. Интерфейс или абстрактный класс");
        System.out.println("Решения:");
        System.out.println("- Identifiable: interface (чистый контракт, нет состояния)");
        System.out.println("- CoursePolicy: interface (стратегия, можно лямбда)");
        System.out.println("- BaseEntity: abstract class (общее состояние id, createdAt)");
        System.out.println("- Formatter: interface (поведение без состояния)");
        System.out.println("- ClockProvider: interface (для мокинга в тестах)");
        System.out.println();

        System.out.println("Задание 6. Переопределение без случайной перегрузки");
        demonstrateAccidentalOverload();
        System.out.println();

        System.out.println("Задание 7. Композиция вместо наследования");
        demonstrateDecorators();
    }

    private static void demonstrateAccidentalOverload() {
        System.out.println("--- Без @Override (случайная перегрузка) ---");
        BaseProcessor base = new DerivedProcessor();
        base.process("test"); // Вызовет BaseProcessor.process(String), а не DerivedProcessor.process(Object)!

        System.out.println("--- С правильным @Override ---");
        CorrectBaseProcessor correctBase = new CorrectDerivedProcessor();
        correctBase.process("test"); // Вызовет CorrectDerivedProcessor.process(String)
    }

    private static void demonstrateDecorators() {
        NotificationSender emailSender = new EmailSender();

        System.out.println("Порядок 1: Metrics -> Logging -> Email");
        NotificationSender sender1 = new MetricsSenderDecorator(
            new LoggingSenderDecorator(emailSender)
        );
        sender1.send("Test message", "admin@site.com");
        System.out.println();

        System.out.println("Порядок 2: Logging -> Metrics -> Email");
        NotificationSender sender2 = new LoggingSenderDecorator(
            new MetricsSenderDecorator(emailSender)
        );
        sender2.send("Test message", "admin@site.com");
    }
}

// Ошибочный пример (без @Override) - случайная перегрузка
class BaseProcessor {
    public void process(String data) {
        System.out.println("Base: String");
    }
}

class DerivedProcessor extends BaseProcessor {
    // Хотели переопределить, но изменили тип параметра - это перегрузка!
    public void process(Object data) {
        System.out.println("Derived: Object");
    }
}

// Правильный пример с @Override
class CorrectBaseProcessor {
    public void process(String data) {
        System.out.println("CorrectBase: String");
    }
}

class CorrectDerivedProcessor extends CorrectBaseProcessor {
    @Override
    public void process(String data) {
        System.out.println("CorrectDerived: String");
    }
}