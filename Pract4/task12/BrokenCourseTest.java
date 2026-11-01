import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// ЧЕТЫРЕ хрупких места. Этот класс НЕ входит в обычный прогон
// (исключен в pom.xml), он нужен, чтобы увидеть отказ.
// Запуск: mvn -Ptask12 test -Dsurefire.excludes=
//
// Никакого @BeforeEach с очисткой: состояние общее, и это проблема №1 и №2.
// Проблему №3 maven маскирует: surefire всегда запускает тесты
// с рабочей директорией в корне проекта. Прогнав этот тест из другой
// директории (IDE, обычный java -cp), он падает с NoSuchFileException.
class BrokenCourseTest {
  // Проблема 1: тест зависит от порядка - он ищет запись, которую
  // должен был сохранить другой тест. Запущенный по одному, он не падает
  // только потому, что запись осталась от предыдущего прогона.
  @Test
  void find_returnsRecordSavedByAnotherTest() {
    CourseRecord found = StaticCourseRegistry.find(1);

    assertNotNull(found, "record was saved by save_persistsRecord");
    assertEquals("Java", found.title());
  }

  // Проблема 2: static state протекает между тестами. Тест проходит,
  // если он первый, и падает, если перед ним отработал любой другой.
  @Test
  void callLog_containsOnlyItsOwnSaves() {
    StaticCourseRegistry.save(new CourseRecord(7, "Go", 16));

    assertEquals(List.of("save 7"), StaticCourseRegistry.callLog());
  }

  // Проблема 3: путь собран из текущей директории. Работает только если
  // процесс запущен из корня репозитория - из task12/ или из IDE упадет.
  @Test
  void catalog_isLoadedFromWorkingDirectory() throws Exception {
    List<CourseRecord> records =
        CourseCatalog.loadCsv(Path.of("task12", "data", "courses.csv"));

    assertEquals(2, records.size());
  }

  // Проблема 4: тест прибит к тексту toString. Любое изменение формата
  // (новое поле, другой порядок) роняет тест, хотя поведение не изменилось.
  @Test
  void record_toStringMatchesSnapshot() {
    CourseRecord record = new CourseRecord(1, "Java", 32);

    assertEquals("CourseRecord[id=1, title=Java, hours=32]", record.toString());
  }

  // сам тест, на который смотрят другие
  @Test
  void save_persistsRecord() {
    StaticCourseRegistry.save(new CourseRecord(1, "Java", 32));
  }
}