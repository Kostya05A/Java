package task9;
import java.util.*;

public class Pecs {
    // Producer — читаем (extends), Consumer — пишем (super)
    public static <T> void copy(List<? extends T> src, List<? super T> dst) {
        for (T t : src) dst.add(t);
    }

    static class Course { 
        String name; 
        Course(String n) { this.name = n; }
        public String toString() { return name; }
    }
    
    static class NamedCourse extends Course { 
        NamedCourse(String n) { super(n); }
    }
    public static void main(String[] args) {
        List<NamedCourse> src = List.of(
            new NamedCourse("A"), 
            new NamedCourse("B")
        );
        List<Course> dst = new ArrayList<>();
        copy(src, dst);
        System.out.println("Скопировано: " + dst); // T = Course, src: ? extends Course, dst: ? super Course
    }
}
