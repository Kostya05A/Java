import java.util.LinkedHashMap;
import java.util.Map;

import src.Course;

// CourseStore публикует результат только целиком.
// Записи не добавляются частично: либо импорт успешен, либо ничего не изменилось.
public class CourseStore {
  private final Map<Long, Course> courses = new LinkedHashMap<>();

  public void publish(java.util.Collection<Course> imported) {
    if (imported == null) throw new IllegalArgumentException("imported must not be null");
    // атомарная публикация: сначала проверяем, что можно опубликовать,
    // затем заменяем содержимое
    Map<Long, Course> copy = new LinkedHashMap<>(imported.size());
    for (Course c : imported) {
      if (copy.putIfAbsent(c.id(), c) != null) {
        throw new IllegalArgumentException("duplicate id: " + c.id());
      }
    }
    courses.clear();
    courses.putAll(copy);
  }

  public Course get(long id) {
    return courses.get(id);
  }

  public int size() {
    return courses.size();
  }

  public void clear() {
    courses.clear();
  }
}