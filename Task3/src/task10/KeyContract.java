package task10;
import java.util.*;

public class KeyContract {
    static final class Course {
        final int id;
        String title;          // НЕ участвует в равенстве
        String description;    // включим в hash намеренно — ошибка

        Course(int id, String title, String desc) {
            this.id = id; 
            this.title = title; 
            this.description = desc;
        }
        // правильное equals — только по id
        public boolean equals(Object o) {
            return o instanceof Course c && c.id == id;
        }
        // ОШИБКА: hashCode зависит от description
        public int hashCode() { 
            return Objects.hash(id, description); 
        }
        
        public String toString() {
            return "Course[" + id + "]";
        }
    }

    public static void main(String[] args) {
        Set<Course> set = new HashSet<>();
        Course c = new Course(1, "Algo", "intro");
        set.add(c);
        System.out.println("До изменения: " + set.contains(c)); // true

        // меняем поле, участвующее в hashCode, но не в equals
        c.description = "advanced";
        // Контракт нарушен: equals не изменился, hashCode — изменился.
        System.out.println("После изменения: " + set.contains(c)); // FALSE! Ключ «потерян»
    }
}
