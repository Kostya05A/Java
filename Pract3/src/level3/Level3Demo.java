package src.level3;

import src.Course;

public class Level3Demo {
    public static void main(String[] args) {
        System.out.println("Задание 8. Нарушение LSP");
        demonstrateLSP();
        System.out.println();

        System.out.println("Задание 9. Политика допуска как стратегия");
        demonstrateEnrollmentPolicy();
        System.out.println();

        System.out.println(" Задание 10. Конфликт default-метода");
        demonstrateDefaultConflict();
    }

    private static void demonstrateLSP() {
        System.out.println("ReadOnlyCourseRepository не наследует save()");
        System.out.println("Клиент работает только с ReadableCourseRepository:");

        ReadableCourseRepository readOnlyRepo = new ReadOnlyCourseRepository() {
            @Override
            public Course findById(long id) {
                return new Course(id, "Test Course", 10);
            }
        };

        Course course = readOnlyRepo.findById(1);
        System.out.println("Found: " + course.title());
    }

    private static void demonstrateEnrollmentPolicy() {
        Course course = new Course(1, "Advanced Java", 40);

        EnrollmentService service1 = new EnrollmentService(new AgePolicy(18));
        service1.enroll("Alice", course);

        EnrollmentService service2 = new EnrollmentService(new PrerequisitesPolicy());
        service2.enroll("Bob", course);
    }

    private static void demonstrateDefaultConflict() {
        NotifierA notifier = new ConcreteNotifier();
        notifier.notify("Test");
    }
}

class ConcreteNotifier implements NotifierA, NotifierB {
    @Override
    public void notify(String message) {
        NotifierA.super.notify(message);
    }
}