import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Так выглядит "глобальное состояние", на котором ломаются тесты:
// данные живут в static-поле, а не в объекте.
// Тесты вынуждены вызывать clear() в @BeforeEach, потому что иначе видят
// чужие записи, а в случайном порядке всё равно падают.
// CourseRepository - версия без static, она и используется в RobustCourseTest.
public class CourseRepository {
  private final Map<Long, CourseRecord> records = new LinkedHashMap<>();
  private int saveCalls;

  public void save(CourseRecord record) {
    records.put(record.id(), record);
    saveCalls++;
  }

  public CourseRecord find(long id) {
    return records.get(id);
  }

  public List<CourseRecord> findAll() {
    return List.copyOf(records.values());
  }

  public int count() {
    return records.size();
  }

  // для теста на "повторное сохранение того же id" - состояние видно и извне
  public int saveCalls() {
    return saveCalls;
  }
}

class StaticCourseRegistry {
  private static final Map<Long, CourseRecord> RECORDS = new LinkedHashMap<>();
  private static final List<String> CALL_LOG = new ArrayList<>();

  public static void save(CourseRecord record) {
    RECORDS.put(record.id(), record);
    CALL_LOG.add("save " + record.id());
  }

  public static CourseRecord find(long id) {
    return RECORDS.get(id);
  }

  public static int count() {
    return RECORDS.size();
  }

  public static List<String> callLog() {
    return List.copyOf(CALL_LOG);
  }

  public static void clear() {
    RECORDS.clear();
    CALL_LOG.clear();
  }
}