package task13;
import java.util.*;

public class InverseIndex {
    // Tag -> Set<CourseId>
    private final Map<String, Set<Integer>> idx = new HashMap<>();
    // CourseId -> Set<Tag> — для быстрого удаления курса
    private final Map<Integer, Set<String>> courseTags = new HashMap<>();

    public void addCourse(int courseId, Set<String> tags) {
        Set<String> prev = courseTags.putIfAbsent(courseId, new HashSet<>());
        Set<String> ct = (prev != null) ? prev : courseTags.get(courseId);
        for (String t : tags) {
            ct.add(t);
            idx.computeIfAbsent(t, k -> new HashSet<>()).add(courseId);
        }
    }

    public void removeCourse(int courseId) {
        Set<String> tags = courseTags.remove(courseId);
        if (tags == null) return;
        for (String t : tags) {
            Set<Integer> s = idx.get(t);
            if (s != null) {
                s.remove(courseId);
                if (s.isEmpty()) idx.remove(t); // пустые set не остаются
            }
        }
    }

    /** Пересечение: курсы, имеющие ВСЕ указанные теги. */
    public Set<Integer> intersect(Set<String> tags) {
        if (tags.isEmpty()) return Set.of();
        Iterator<String> it = tags.iterator();
        // стартуем с самого маленького множества — экономия
        Set<Integer> acc = new HashSet<>(idx.getOrDefault(it.next(), Set.of()));
        while (it.hasNext() && !acc.isEmpty()) {
            acc.retainAll(idx.getOrDefault(it.next(), Set.of()));
        }
        return acc;
    }
}
