package task7;
import java.util.*;

public class GenericMethodsDemo {
    record Student(int id, String name) {}
    
    public static void main(String[] args) {
        List<String> words = List.of("hello", "world");
        System.out.println("firstOrThrow: " + GenericMethods.firstOrThrow(words));
        
        List<String> mutable = new ArrayList<>(words);
        GenericMethods.swap(mutable, 0, 1);
        System.out.println("after swap: " + mutable);
        
        List<Student> students = List.of(
            new Student(1, "Alice"),
            new Student(2, "Bob")
        );
        Map<Integer, Student> index = GenericMethods.indexBy(students, Student::id);
        System.out.println("index: " + index);
        
        // Конфликт ключей:
        List<Student> dups = List.of(
            new Student(1, "Alice"),
            new Student(1, "Bob")
        );
        try {
            GenericMethods.indexBy(dups, Student::id);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
