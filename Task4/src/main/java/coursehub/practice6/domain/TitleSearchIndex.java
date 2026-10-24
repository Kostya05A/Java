package coursehub.practice6.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// поиск по нормализованному ключу: разные исходные строки могут попасть в один ключ
public final class TitleSearchIndex {
  private final Map<String, List<CourseTitle>> byKey = new HashMap<>();

  public void add(CourseTitle title) {
    byKey.computeIfAbsent(title.searchKey(), key -> new ArrayList<>()).add(title);
  }

  public List<CourseTitle> find(String query) {
    return byKey.getOrDefault(CourseTitle.searchKeyOf(query), List.of());
  }

  // конфликт: в одном ключе оказалось больше одной исходной строки
  public boolean hasCollision(String query) {
    return find(query).size() > 1;
  }
}
