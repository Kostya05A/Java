import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import src.Course;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CourseImporterTest {
  @Test
  void importsValid() throws Exception {
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    StringReader reader = new StringReader("1;Java;32\n2;Kotlin;24\n");
    int processed = importer.importFrom(reader);
    assertEquals(2, processed);
    assertEquals(2, store.size());
  }

  @Test
  void malformedThrowsWithRecordNumberAndCause() {
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    StringReader reader = new StringReader("1;Java;32\n2;Kotlin;bad\n");
    CourseImportException e = assertThrows(CourseImportException.class,
        () -> importer.importFrom(reader));
    assertEquals(2, e.recordNumber());
    assertNotNull(e.getCause());
    assertEquals(0, store.size());
  }

  @Test
  void partialNotPublished() {
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    StringReader reader = new StringReader("1;Java;32\nbad\n2;Kotlin;24\n");
    assertThrows(CourseImportException.class, () -> importer.importFrom(reader));
    assertEquals(0, store.size());
  }

  @Test
  void malformedAndCloseFailure_suppressed() {
    String[] lines = {"1;Java;32", "2;bad"};
    FailingReader reader = new FailingReader(lines, 2, true);
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    CourseImportException e = assertThrows(CourseImportException.class,
        () -> importer.importFrom(reader));
    assertEquals(2, e.recordNumber());
    assertEquals(1, e.getSuppressed().length);
    assertTrue(e.getSuppressed()[0] instanceof java.io.IOException);
    assertTrue(reader.closed());
    assertEquals(0, store.size());
  }

  @Test
  void closeFailureOnly_io() {
    String[] lines = {"1;Java;32"};
    FailingReader reader = new FailingReader(lines, 100, true);
    CourseStore store = new CourseStore();
    CourseImporter importer = new CourseImporter(store);
    assertThrows(java.io.IOException.class, () -> importer.importFrom(reader));
    assertTrue(reader.closed());
  }
}