import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Те же проверки после починки. Ни очистки, ни фикстур, ни полного toString:
// каждый тест можно запустить отдельно и в любом порядке
// (в pom.xml включен surefire runOrder=random).
class RobustCourseTest {
  // Проблемы 1 и 2: состояние принадлежит объекту, а не static-полю,
  // поэтому тесты не видят друг друга и ничего чистить не нужно
  @Test
  void find_returnsRecordSavedInSameRepository() {
    // Arrange
    CourseRepository repository = new CourseRepository();
    repository.save(new CourseRecord(1, "Java", 32));

    // Act
    CourseRecord found = repository.find(1);

    // Assert
    assertNotNull(found);
    assertEquals("Java", found.title());
    assertEquals(1, repository.count());
  }

  @Test
  void savedRecordIsNotVisibleInAnotherRepository() {
    // Arrange
    CourseRepository repository = new CourseRepository();
    repository.save(new CourseRecord(1, "Java", 32));

    // Act
    CourseRepository another = new CourseRepository();

    // Assert: чужое состояние не протекает
    assertNull(another.find(1), "state leaked between tests");
    assertEquals(0, another.count());
  }

  @Test
  void savingSameIdTwiceReplacesRecordWithoutGrowingState() {
    // Arrange
    CourseRepository repository = new CourseRepository();

    // Act
    repository.save(new CourseRecord(1, "Java", 32));
    repository.save(new CourseRecord(1, "Java v2", 40));

    // Assert
    assertEquals(1, repository.count());
    assertEquals(2, repository.saveCalls(), "both saves happened");
    assertEquals(40, repository.find(1).hours());
  }

  @Test
  void save_keepsInsertionOrder() {
    // Arrange
    CourseRepository repository = new CourseRepository();

    // Act
    repository.save(new CourseRecord(1, "Java", 32));
    repository.save(new CourseRecord(2, "Kotlin", 24));

    // Assert
    assertEquals(List.of(1L, 2L),
        repository.findAll().stream().map(CourseRecord::id).toList());
  }

  // Проблема 3: файл создает сам тест во временной директории.
  // Не нужны ни текущая директория, ни файлы в репозитории.
  @Test
  void catalog_loadsRecordsFromGivenPath(@TempDir Path tmp) throws IOException {
    // Arrange
    Path file = tmp.resolve("courses.csv");
    Files.writeString(file, "1;Java;32\n2;Kotlin;24\n");

    // Act
    List<CourseRecord> records = CourseCatalog.loadCsv(file);

    // Assert
    assertEquals(2, records.size());
    assertEquals("Java", records.get(0).title());
    assertEquals(24, records.get(1).hours());
  }

  @Test
  void catalog_ignoresBlankLines(@TempDir Path tmp) throws IOException {
    // Arrange
    Path file = tmp.resolve("courses.csv");
    Files.writeString(file, "1;Java;32\n\n2;Kotlin;24\n");

    // Act
    List<CourseRecord> records = CourseCatalog.loadCsv(file);

    // Assert
    assertEquals(2, records.size());
  }

  // Проблема 4: проверяем значения, а не текст toString.
  // toString - это вывод для человека, его формат не является контрактом.
  @Test
  void record_exposesItsValuesInsteadOfDependingOnToString() {
    // Arrange
    CourseRecord record = new CourseRecord(1, "Java", 32);

    // Act + Assert
    assertEquals(1, record.id());
    assertEquals("Java", record.title());
    assertEquals(32, record.hours());
  }

  @Test
  void record_toStringMentionsTitleAndHours() {
    // Arrange
    CourseRecord record = new CourseRecord(1, "Java", 32);

    // Act
    String text = record.toString();

    // Assert: интересует только смысл, а не точная строка
    assertTrue(text.contains("Java"), "toString should mention the title");
    assertTrue(text.contains("32"), "toString should mention the hours");
  }
}