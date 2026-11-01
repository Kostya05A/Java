package src.level1;

import src.Course;

public class Level1Demo {
    public static void main(String[] args) {
        System.out.println("Задание 1. Один контракт, две реализации");
        Course course = new Course(10, "Java Fundamentals", 32);
        course.completeHours(6);

        CourseFormatter formatter;
        
        formatter = new CompactCourseFormatter();
        System.out.println(formatter.format(course));
        System.out.println();
        
        formatter = new DetailedCourseFormatter();
        System.out.println(formatter.format(course));
        System.out.println();

        System.out.println("Задание 2. Dynamic dispatch");
        NotificationSender ref1 = new EmailSender();
        EmailSender ref2 = new EmailSender();

        ref1.send("Hello");
        ref2.send("Hello", "admin@site.com");
        System.out.println();

        System.out.println("Задание 3. Полиморфный массив");
        CourseFormatter[] formatters = {
            new CompactCourseFormatter(),
            new DetailedCourseFormatter(),
            new SummaryCourseFormatter()
        };
        
        for (CourseFormatter f : formatters) {
            System.out.println(f.format(course));
            System.out.println();
        }
    }
}