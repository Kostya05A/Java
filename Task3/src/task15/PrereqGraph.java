package task15;
import java.util.*;

public final class PrereqGraph<T> {
    // вершина -> множество непосредственных пререквизитов
    private final Map<T, Set<T>> prereqs = new LinkedHashMap<>();

    public void addVertex(T v) {
        Objects.requireNonNull(v);
        prereqs.computeIfAbsent(v, k -> new LinkedHashSet<>());
    }

    public void addEdge(T course, T prerequisite) {
        Objects.requireNonNull(course);
        Objects.requireNonNull(prerequisite);
        if (!prereqs.containsKey(course)) addVertex(course);
        if (!prereqs.containsKey(prerequisite)) addVertex(prerequisite);
        prereqs.get(course).add(prerequisite);
    }

    /** Kahn's algorithm на LinkedHashSet/LinkedHashMap => детерминированный порядок. */
    public List<T> topologicalOrder() {
        Map<T, Integer> indeg = new LinkedHashMap<>();
        for (T v : prereqs.keySet()) indeg.put(v, 0);
        for (Map.Entry<T, Set<T>> e : prereqs.entrySet())
            for (T p : e.getValue()) indeg.merge(p, 0, Integer::sum); // гарантируем наличие
        for (Map.Entry<T, Set<T>> e : prereqs.entrySet())
            for (T p : e.getValue()) indeg.merge(e.getKey(), 1, Integer::sum); // нет, пересчитаем ниже

        // корректный подсчёт:
        indeg.replaceAll((k, v) -> 0);
        for (Map.Entry<T, Set<T>> e : prereqs.entrySet())
            for (T p : e.getValue()) indeg.merge(e.getKey(), 1, Integer::sum);
        // indeg[v] = число пререквизитов v

        // очередь с естественным порядком вставки (детерминизм)
        Deque<T> ready = new ArrayDeque<>();
        for (Map.Entry<T, Integer> e : indeg.entrySet())
            if (e.getValue() == 0) ready.addLast(e.getKey());

        List<T> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            T v = ready.removeFirst();
            order.add(v);
            // найдём всех, кто зависит от v
            for (Map.Entry<T, Set<T>> e : prereqs.entrySet()) {
                if (e.getValue().contains(v)) {
                    T dep = e.getKey();
                    int nd = indeg.get(dep) - 1;
                    indeg.put(dep, nd);
                    if (nd == 0) ready.addLast(dep);
                }
            }
        }
        if (order.size() != indeg.size()) throw new CycleException(findCycle());
        return order;
    }

    /** DFS-поиск цикла с восстановлением пути. */
    private List<T> findCycle() {
        Set<T> done = new HashSet<>();
        Set<T> onStack = new LinkedHashSet<>();
        Deque<T> path = new ArrayDeque<>();

        for (T start : prereqs.keySet()) {
            if (done.contains(start)) continue;
            Deque<T> cycle = dfs(start, done, onStack, path);
            if (cycle != null) return new ArrayList<>(cycle);
        }
        return List.of();
    }

    private Deque<T> dfs(T v, Set<T> done, Set<T> onStack, Deque<T> path) {
        onStack.add(v);
        path.addLast(v);
        for (T p : prereqs.getOrDefault(v, Set.of())) {
            if (onStack.contains(p)) {
                // восстановим путь от p до p
                Deque<T> cycle = new ArrayDeque<>();
                boolean take = false;
                for (T x : path) {
                    if (x.equals(p)) take = true;
                    if (take) cycle.addLast(x);
                }
                cycle.addLast(p);
                return cycle;
            }
            if (!done.contains(p)) {
                Deque<T> r = dfs(p, done, onStack, path);
                if (r != null) return r;
            }
        }
        path.removeLast();
        onStack.remove(v);
        done.add(v);
        return null;
    }

    public static final class CycleException extends RuntimeException {
        public final List<?> cycle;
        CycleException(List<?> cycle) { super("cycle: " + cycle); this.cycle = cycle; }
    }
}
