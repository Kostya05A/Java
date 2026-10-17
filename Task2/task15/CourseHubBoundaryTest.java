import org.junit.jupiter.api.Test;
import src.Course;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class CourseHubBoundaryTest {
  @Test
  void hubTranslatesDomainErrorsToStableErrorCodes() {
    CourseStore store = new CourseStore();
    // preload course CLOSED
    Course closed = new Course(1, "Java", 32);
    closed.setStatus(Course.Status.CLOSED);
    closed.setCapacity(5);
    closed.setHasPrerequisite(true);
    store.publish(java.util.List.of(closed));

    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.EnrollResult r = hub.enroll(42, 1);
    assertFalse(r.isOk());
    assertEquals(ErrorCode.ENROLL_CLOSED, r.errorCode());
    assertNotNull(r.message());
  }

  @Test
  void hubReturnsImportMalformedWithCausePreserved() {
    CourseStore store = new CourseStore();
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.ImportResult r = hub.importCourses(new StringReader("1;Java;bad"));
    assertFalse(r.isOk());
    assertEquals(ErrorCode.IMPORT_MALFORMED_RECORD, r.errorCode());
    assertNotNull(r.cause());
    assertInstanceOf(CourseImportException.class, r.cause());
    // cause сохраняется
    assertTrue(r.cause() != null);
  }

  @Test
  void hubReturnsImportMalformedOnBadRecord() {
    CourseStore store = new CourseStore();
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.ImportResult r = hub.importCourses(new StringReader("1;Java;32\n2;Kotlin;oops"));
    assertFalse(r.isOk());
    assertEquals(ErrorCode.IMPORT_MALFORMED_RECORD, r.errorCode());
  }

  @Test
  void hubEnrollFull() {
    CourseStore store = new CourseStore();
    Course c = new Course(1, "Java", 32);
    c.setStatus(Course.Status.OPEN);
    c.setCapacity(0);
    c.setHasPrerequisite(true);
    store.publish(java.util.List.of(c));
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.EnrollResult r = hub.enroll(42, 1);
    assertFalse(r.isOk());
    assertEquals(ErrorCode.ENROLL_FULL, r.errorCode());
  }

  @Test
  void hubEnrollPrereqMissing() {
    CourseStore store = new CourseStore();
    Course c = new Course(1, "Java", 32);
    c.setStatus(Course.Status.OPEN);
    c.setCapacity(5);
    c.setHasPrerequisite(false);
    store.publish(java.util.List.of(c));
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.EnrollResult r = hub.enroll(42, 1);
    assertFalse(r.isOk());
    assertEquals(ErrorCode.ENROLL_PREREQ_MISSING, r.errorCode());
  }

  @Test
  void hubEnrollAlready() throws EnrollmentRejectedException {
    CourseStore store = new CourseStore();
    Course c = new Course(1, "Java", 32);
    c.setStatus(Course.Status.OPEN);
    c.setCapacity(5);
    c.setHasPrerequisite(true);
    c.addEnrolled(42);
    store.publish(java.util.List.of(c));
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.EnrollResult r = hub.enroll(42, 1);
    assertFalse(r.isOk());
    assertEquals(ErrorCode.ENROLL_ALREADY, r.errorCode());
  }

  @Test
  void hubImportSuccess() {
    CourseStore store = new CourseStore();
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.ImportResult r = hub.importCourses(new StringReader("1;Java;32\n"));
    assertTrue(r.isOk());
    assertEquals(1, r.processed());
  }

  @Test
  void hubEnrollNotFoundGivesInvalidArgument() {
    CourseStore store = new CourseStore();
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.EnrollResult r = hub.enroll(42, 999);
    assertFalse(r.isOk());
    assertEquals(ErrorCode.HUB_INVALID_ARGUMENT, r.errorCode());
  }

  @Test
  void resourcesClosedOnImport_malformedWithCloseFailure() {
    String[] lines = {"1;Java;32", "bad"};
    FailingReader reader = new FailingReader(lines, 2, true);
    CourseStore store = new CourseStore();
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.ImportResult r = hub.importCourses(reader);
    assertFalse(r.isOk());
    assertTrue(reader.closed());
  }

  @Test
  void hubImportIoOnClose() {
    String[] lines = {"1;Java;32"};
    FailingReader reader = new FailingReader(lines, 100, true);
    CourseStore store = new CourseStore();
    CourseHub hub = new CourseHub(new CourseImporter(store), store, new EnrollmentService());
    CourseHub.ImportResult r = hub.importCourses(reader);
    assertFalse(r.isOk());
    assertEquals(ErrorCode.IMPORT_IO_ERROR, r.errorCode());
    assertTrue(reader.closed());
  }
}