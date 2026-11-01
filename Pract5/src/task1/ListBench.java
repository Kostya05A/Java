package task1;
import java.util.*;

public class ListBench {
    static long bench(Runnable r) {
        long t = System.nanoTime();
        r.run();
        return System.nanoTime() - t;
    }

    public static void main(String[] args) {
        int N = 200_000;
        List<Integer> al = new ArrayList<>();
        List<Integer> ll = new LinkedList<>();
        for (int i = 0; i < N; i++) { al.add(i); ll.add(i); }

        // чтение по индексу (случайные индексы)
        Random rnd = new Random(1);
        long alRead = bench(() -> {
            int s = 0;
            for (int i = 0; i < N; i++) s += al.get(rnd.nextInt(N));
        });
        long llRead = bench(() -> {
            int s = 0;
            for (int i = 0; i < N; i++) s += ll.get(rnd.nextInt(N));
        });

        // обход (итератор / for-each)
        long alWalk = bench(() -> { int s = 0; for (int x : al) s += x; });
        long llWalk = bench(() -> { int s = 0; for (int x : ll) s += x; });

        System.out.printf("Чтение по индексу: AL=%d ms, LL=%d ms%n", 
            alRead/1_000_000, llRead/1_000_000);
        System.out.printf("Обход: AL=%d ms, LL=%d ms%n", 
            alWalk/1_000_000, llWalk/1_000_000);
    }
}