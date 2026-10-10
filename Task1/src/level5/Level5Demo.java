package src.level5;

import src.Course;
import java.util.List;

public class Level5Demo {
    public static void main(String[] args) {
        System.out.println("Задание 13. Удаление type code");
        demonstrateTypeCodeRemoval();
        System.out.println();

        System.out.println("Задание 14. Наследование взрывает комбинации");
        demonstrateComposition();
        System.out.println();

        System.out.println("Задание 15. Расширяемый движок правил");
        demonstrateRuleEngine();
    }

    private static void demonstrateTypeCodeRemoval() {
        System.out.println("Вместо switch по notificationType используем полиморфизм");
        System.out.println("Каждый тип уведомления - отдельная реализация интерфейса");
    }

    private static void demonstrateComposition() {
        CourseFormat online = new OnlineFormat();
        PaymentPolicy free = new FreePayment();

        online.conduct();
        free.charge();
        System.out.println();

        CourseFormat classroom = new ClassroomFormat();
        PaymentPolicy fixed = new FixedPayment();

        classroom.conduct();
        fixed.charge();
        System.out.println();

        CourseFormat hybrid = new HybridFormat();
        PaymentPolicy subscription = new SubscriptionPayment();

        hybrid.conduct();
        subscription.charge();
    }

    private static void demonstrateRuleEngine() {
        Course course = new Course(1, "Advanced Java", 40);

        EnrollmentRule ageRule = new AgeRule();
        EnrollmentRule prereqRule = new PrerequisiteRule();
        EnrollmentRule capacityRule = new CapacityRule();

        EnrollmentRule allOfRule = new AllOfRule(List.of(ageRule, prereqRule, capacityRule));

        RuleResult result = allOfRule.evaluate("Alice", course);

        switch (result) {
            case Pass p -> System.out.println("Enrollment passed!");
            case Fail f -> System.out.println("Enrollment failed: " + f.reason());
        }
    }
}