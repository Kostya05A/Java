package task4;
import java.util.*;

public class Waitlist {
    public static void main(String[] args) {
        Queue<String> waitlist = new ArrayDeque<>();

        // add vs offer: add бросает IllegalStateException при отказе,
        // offer возвращает false. Для bounded-очередей разница важна;
        // для ArrayDeque обе работают (очередь не ограничена).
        waitlist.offer("alice");
        waitlist.offer("bob");
        waitlist.add("carol");

        // element vs peek: element бросает NoSuchElementException, peek -> null
        System.out.println("Следующий (peek): " + waitlist.peek());   // alice, не удаляя
        System.out.println("Голова (element): " + waitlist.element()); // alice, не удаляя

        // remove vs poll: remove бросает, poll -> null
        System.out.println("Обслужен (poll): " + waitlist.poll());  // alice
        System.out.println("Обслужен (remove): " + waitlist.remove()); // bob

        // обработка пустой
        waitlist.poll(); // carol
        if (waitlist.isEmpty()) System.out.println("Очередь пуста");
        // безопасный доступ:
        String next = waitlist.peek(); // null, без исключения
        System.out.println("Безопасный доступ: " + next);
    }
}