package task1;
import java.util.*;

public class ListDemo {
    public static void main(String[] args) {
        List<String> regs = new ArrayList<>();
        regs.add("alice");
        regs.add("bob");
        regs.add("carol");
        regs.add("dave");
        regs.add("carol");

        // вставка в середину, удаление по индексу
        regs.add(2, "zoe");
        String removed = regs.remove(1); // "bob"

        // поиск дубликатов
        Set<String> seen = new HashSet<>();
        List<String> dups = new ArrayList<>();
        for (String s : regs) if (!seen.add(s)) dups.add(s);

        System.out.println("Список: " + regs);
        System.out.println("Дубликаты: " + dups);
    }
}