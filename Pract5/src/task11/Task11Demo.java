package task11;
import java.util.*;

public class Task11Demo {
    record Course(int id, String name) {}
    record Player(String name, int score) {}
    record Event(int timestamp, String description) {}

    public static void main(String[] args) {
        // 1. Каталог по ID - HashMap
        Map<Integer, Course> catalog = new HashMap<>();
        catalog.put(101, new Course(101, "Algorithms"));
        System.out.println("1. Каталог: " + catalog.get(101));

        // 2. Leaderboard - TreeSet
        TreeSet<Player> leaderboard = new TreeSet<>(
            Comparator.comparingInt(Player::score).reversed()
                      .thenComparing(Player::name)
        );
        leaderboard.add(new Player("Alice", 1500));
        leaderboard.add(new Player("Bob", 1200));
        System.out.println("2. Топ игрок: " + leaderboard.first());

        // 3. Очередь - ArrayDeque
        Queue<String> waitlist = new ArrayDeque<>();
        waitlist.offer("Task-1");
        waitlist.offer("Task-2");
        System.out.println("3. Следующая задача: " + waitlist.poll());

        // 4. Уникальные теги - LinkedHashSet
        Set<String> tags = new LinkedHashSet<>();
        tags.add("java");
        tags.add("collections");
        tags.add("java");
        System.out.println("4. Теги: " + tags);

        // 5. Диапазонный поиск - TreeMap
        NavigableMap<Integer, Event> timeline = new TreeMap<>();
        timeline.put(1000, new Event(1000, "Start"));
        timeline.put(2000, new Event(2000, "Process"));
        System.out.println("5. Диапазон: " + timeline.subMap(500, true, 1500, true));

        // 6. Доступ по индексу - ArrayList
        List<String> items = new ArrayList<>();
        items.add("First");
        items.add("Second");
        System.out.println("6. По индексу: " + items.get(1));
    }
}