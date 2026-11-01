package task2;
import java.util.*;

record CourseCode(String subject, int number) {
    // record автоматически генерирует equals/hashCode по всем полям
}

public class SetDemo {
    public static void main(String[] args) {
        List<CourseCode> seq = List.of(
            new CourseCode("CS", 101),
            new CourseCode("MATH", 201),
            new CourseCode("CS", 101),   // дубликат
            new CourseCode("PHYS", 110),
            new CourseCode("MATH", 201)
        );

        // LinkedHashSet сохраняет порядок первого появления
        Set<CourseCode> unique = new LinkedHashSet<>(seq);
        System.out.println("Уникальные: " + unique); // [CS/101, MATH/201, PHYS/110]
    }
}