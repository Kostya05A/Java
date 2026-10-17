import org.junit.jupiter.api.Test;
import src.Course;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

// Подавать частично — нельзя. Либо всё, либо ничего.
// При двух отказах (основной и close) основной должен быть primary,
// а close — suppressed.
class CourseImporterTest {
  @Test
  void importsAllValidRecordsAndPublishesAtomically() throws Exception {
    // Arrange
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    StringReader reader = new StringReader(
        "1;Java;32\n" +
            "2;Kotlin;24\n" +
            "# comment\n" +
            "\n" +
            "3;SQL;16\n");

    // Act
    int processed = importer.importFrom(reader);

    // Assert
    assertEquals(3, processed);
    assertEquals(3, store.size());
    assertEquals("Java", store.get(1).title());
    assertEquals(24, store.get(2).durationHours());
    assertEquals(16, store.get(3).durationHours());
  }

  @Test
  void malformedRecordThrowsCourseImportExceptionWithRecordNumberAndCause() {
    // Arrange
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    // record 3 malformed (не число hours)
    StringReader reader = new StringReader(
        "1;Java;32\n" +
            "2;Kotlin;24\n" +
            "3;SQL;bad\n");

    // Act
    CourseImportException e = assertThrows(
        CourseImportException.class,
        () -> importer.importFrom(reader));

    // Assert: номер записи сохраняется и причина (cause) тоже
    assertEquals(3, e.recordNumber());
    assertNotNull(e.getCause(), "cause must be preserved");
    // до ошибки ничего не опубликовано
    assertEquals(0, store.size());
  }

  @Test
  void malformedRecordAndCloseFailure_primaryAndSuppressed() {
    // Arrange: 3-я запись сломана (malformedAt=3) + close падает
    String[] lines = {"1;Java;32", "2;Kotlin;24", "3;SQL;bad"};
    FailingReader reader = new FailingReader(lines, 3, true);
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);

    // Act
    CourseImportException e = assertThrows(
        CourseImportException.class,
        () -> importer.importFrom(reader));

    // Assert: основное исключение - CourseImportException
    assertEquals(3, e.recordNumber());
    // suppressed: IOException от close()
    Throwable[] suppressed = e.getSuppressed();
    assertEquals(1, suppressed.length);
    assertTrue(suppressed[0] instanceof IOException);
    assertEquals("close failed", suppressed[0].getMessage());
    // ресурсы закрыты
    assertTrue(reader.closed());
    // частично не опубликовали
    assertEquals(0, store.size());
  }

  @Test
  void closeFailureOnly_propagatesIOException() {
    // Arrange: все валидные, но close() падает
    String[] lines = {"1;Java;32", "2;Kotlin;24"};
    FailingReader reader = new FailingReader(lines, 100, true);
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);

    // Act
    IOException e = assertThrows(IOException.class,
        () -> importer.importFrom(reader));

    // Assert
    assertEquals("close failed", e.getMessage());
    assertTrue(reader.closed());
    // успешные записи были в буфере, но перед публикацией произошло
    // исключение при закрытии? В try-with-resources close() вызывается
    // после тела: публикация store.publish(buffer) происходит в теле,
    // поэтому при успешном чтении всех записей publish выполнится
    // до close(). Если close() упадет - исключение из close() будет
    // suppressed, если основного не было. Но тут основного нет -
    // IOException "close failed" - primary. Publish уже прошёл?
    // Проверим: если чтение успешно, buffer заполнен, publish(buffer) вызван.
    assertEquals(2, store.size(), "store must be published before close");
  }

  @Test
  void malformedBeforeAnyPublish_preventsPartialPublication() {
    // Arrange: первая же строка после пустых - битая
    StringReader reader = new StringReader("\n\nbad;record;here\n");
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);

    // Act
    CourseImportException e = assertThrows(
        CourseImportException.class,
        () -> importer.importFrom(reader));

    // Assert
    assertEquals(3, e.recordNumber()); // считаем все непустые/не-комментарии? строка "bad;record;here" - 3-я непустая
    assertEquals(0, store.size(), "no partial publish");
  }

  @Test
  void preservesOriginalCauseWhenWrapping() {
    // Arrange
    StringReader reader = new StringReader("1;Java;notanumber\n");
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);

    // Act
    CourseImportException e = assertThrows(
        CourseImportException.class,
        () -> importer.importFrom(reader));

    // Assert
    assertInstanceOf(NumberFormatException.class, e.getCause());
  }

  @Test
  void processedCountMatchesOnlyPublishedOnes() throws Exception {
    // Arrange
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    StringReader reader = new StringReader("1;Java;32\n2;Kotlin;24\n");

    // Act
    int processed = importer.importFrom(reader);

    // Assert: возвращаем количество обработанных (опубликованных) записей
    assertEquals(2, processed);
    assertEquals(processed, store.size());
  }
}