package task5;
import java.util.*;

public class SafeRemove {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));

        // 1) Воспроизведение CME
        try {
            for (String s : list) if ("b".equals(s)) list.remove(s);
        } catch (ConcurrentModificationException e) {
            System.out.println("Поймано CME");
        }

        // 2) Исправление через iterator
        List<String> a = new ArrayList<>(List.of("a", "b", "c", "d"));
        for (Iterator<String> it = a.iterator(); it.hasNext(); ) {
            if ("b".equals(it.next())) it.remove(); // только так!
        }
        System.out.println("После iterator: " + a);

        // 3) Исправление через removeIf (предпочтительно)
        List<String> b = new ArrayList<>(List.of("a", "b", "c", "d"));
        b.removeIf(s -> "b".equals(s));
        System.out.println("После removeIf: " + b);
    }
}
