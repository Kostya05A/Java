package task13;
import java.util.*;

public class InverseIndexDemo {
    public static void main(String[] args) {
        InverseIndex idx = new InverseIndex();
        
        idx.addCourse(1, Set.of("java", "backend"));
        idx.addCourse(2, Set.of("java", "frontend"));
        idx.addCourse(3, Set.of("python", "backend"));
        
        System.out.println("Курсы с 'java': " + idx.intersect(Set.of("java")));
        System.out.println("Курсы с 'java' И 'backend': " + 
            idx.intersect(Set.of("java", "backend")));
        
        idx.removeCourse(1);
        System.out.println("После удаления курса 1, 'java': " + 
            idx.intersect(Set.of("java")));
    }
}