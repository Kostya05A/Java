package task8;
import java.util.*;

public class MaxDemo {
    // bound с учётом подтипов: ? super T, чтобы можно было сравнить T с его надтипом
    public static <T extends Comparable<? super T>> T max(Collection<T> c) {
        if (c == null || c.isEmpty()) throw new NoSuchElementException();
        Iterator<T> it = c.iterator();
        T best = it.next();
        while (it.hasNext()) {
            T x = it.next();
            if (x.compareTo(best) > 0) best = x;
        }
        return best;
    }

    record Version(int major, int minor) implements Comparable<Version> {
        public int compareTo(Version o) {
            int d = Integer.compare(major, o.major);
            return d != 0 ? d : Integer.compare(minor, o.minor);
        }
    }

    public static void main(String[] args) {
        System.out.println("max(Integer): " + max(List.of(3, 1, 4, 1, 5)));
        System.out.println("max(String): " + max(List.of("apple", "zebra", "mango")));
        System.out.println("max(Version): " + max(List.of(
            new Version(1, 2), 
            new Version(2, 0),
            new Version(1, 9)
        )));
    }
}
