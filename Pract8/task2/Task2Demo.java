package task2;

import src.Level;

import java.util.List;
import java.util.Map;

public class Task2Demo {
    public static void main(String[] args) {
        List<CourseWithModules> courses = List.of(
                new CourseWithModules("JAVA101", "Java: основы", Level.BEGINNER, List.of(
                        new Module("Синтаксис", List.of("Переменные", "  циклы ", "Условия")),
                        new Module("ООП", List.of("Классы", "Интерфейсы", "классы"))
                )),
                new CourseWithModules("JAVA201", "Java: коллекции", Level.INTERMEDIATE, List.of(
                        new Module("Коллекции", List.of("List", "Map", "Set")),
                        new Module("Функциональное", List.of("Лямбды", "Stream API"))
                )),
                new CourseWithModules("DB101", "SQL: запросы", Level.BEGINNER, List.of(
                        new Module("SELECT", List.of("Фильтрация", "Сортировка")),
                        new Module("JOIN", List.of("INNER JOIN", "LEFT JOIN", "Сортировка"))
                )),
                new CourseWithModules("WEB101", "HTTP и REST", Level.BEGINNER, List.of(
                        new Module("HTTP", List.of("Методы", "Статусы")),
                        new Module("REST", List.of("Ресурсы", "Методы"))
                )),
                new CourseWithModules("ARCH401", "Архитектура сервисов", Level.ADVANCED, List.of(
                        new Module("Сервисы", List.of("REST", "Очереди")),
                        new Module("Надёжность", List.of("Ретраи", "Идемпотентность"))
                ))
        );

        List<String> topics = TopicIndex.uniqueTopics(courses);
        Map<String, List<String>> index = TopicIndex.reverseIndex(courses);

        System.out.println("Уникальные нормализованные темы (" + topics.size() + "):");
        topics.forEach(topic -> System.out.println("  " + topic));

        System.out.println("\nОбратный индекс topic -> courses:");
        index.forEach((topic, codes) -> System.out.println("  " + topic + " -> " + codes));

        System.out.println("\nВложенные списки не строились вручную: использованы flatMap по modules и topics.");
        System.out.println("Нормализация: trim, lower-case, схлопывание пробелов; затем distinct и сортировка.");
    }
}
