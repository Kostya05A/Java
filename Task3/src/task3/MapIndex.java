package task3;
import java.util.*;

record Course(int id, String title) {

}

public class MapIndex {
    public static void main(String[] args) {
        Map<Integer, Course> index = new HashMap<>();

        List<Course> incoming = List.of(
            new Course(1, "Algorithms"),
            new Course(2, "Databases"),
            new Course(1, "Algorithms v2") // повтор!
        );

        for (Course c : incoming) {
            // Вариант 1: putIfAbsent — вернёт старое значение или null
            Course prev = index.putIfAbsent(c.id(), c);
            if (prev != null) {
                System.err.println("Повтор id=" + c.id() + 
                    ": оставлен '" + prev.title() + "', отклонён '" + c.title() + "'");
            }

            // Вариант 2: computeIfAbsent — ленивая инициализация (тут не нужен,
            // но полезен для Map<K, Collection<V>>)
            // index.computeIfAbsent(c.id(), k -> c);

            // Вариант 3: containsKey + get — два прохода по бакету, хуже putIfAbsent
            // if (!index.containsKey(c.id())) index.put(c.id(), c);
        }
        System.out.println("Индекс: " + index);
    }
}