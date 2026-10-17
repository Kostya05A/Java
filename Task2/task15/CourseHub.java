import src.Course;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CourseHub {
  private final CourseImporter importer;
  private final CourseStore store;
  private final EnrollmentService enrollment;

  public CourseHub(CourseImporter importer, CourseStore store, EnrollmentService enrollment) {
    this.importer = importer;
    this.store = store;
    this.enrollment = enrollment;
  }

  public static class ImportResult {
    private final int processed;
    private final ErrorCode error;
    private final String message;
    private final Throwable cause;

    private ImportResult(int processed, ErrorCode error, String message, Throwable cause) {
      this.processed = processed;
      this.error = error;
      this.message = message;
      this.cause = cause;
    }

    public static ImportResult ok(int processed) {
      return new ImportResult(processed, null, null, null);
    }

    public static ImportResult fail(ErrorCode error, String message, Throwable cause) {
      return new ImportResult(0, error, message, cause);
    }

    public int processed() { return processed; }
    public boolean isOk() { return error == null; }
    public ErrorCode errorCode() { return error; }
    public String message() { return message; }
    public Throwable cause() { return cause; }
  }

  public static class EnrollResult {
    private final boolean ok;
    private final ErrorCode error;
    private final String message;
    private final Throwable cause;

    private EnrollResult(boolean ok, ErrorCode error, String message, Throwable cause) {
      this.ok = ok;
      this.error = error;
      this.message = message;
      this.cause = cause;
    }

    public static EnrollResult ok() { return new EnrollResult(true, null, null, null); }
    public static EnrollResult fail(ErrorCode error, String message, Throwable cause) {
      return new EnrollResult(false, error, message, cause);
    }

    public boolean isOk() { return ok; }
    public ErrorCode errorCode() { return error; }
    public String message() { return message; }
    public Throwable cause() { return cause; }
  }

  public ImportResult importCourses(Reader reader) {
    try {
      int p = importer.importFrom(reader);
      return ImportResult.ok(p);
    } catch (CourseImportException e) {
      return ImportResult.fail(ErrorCode.IMPORT_MALFORMED_RECORD, e.getMessage(), e);
    } catch (IOException e) {
      return ImportResult.fail(ErrorCode.IMPORT_IO_ERROR, e.getMessage(), e.getCause());
    } catch (IllegalArgumentException e) {
      return ImportResult.fail(ErrorCode.HUB_INVALID_ARGUMENT, e.getMessage(), e.getCause());
    } catch (IllegalStateException e) {
      return ImportResult.fail(ErrorCode.HUB_UNEXPECTED, e.getMessage(), e.getCause());
    } catch (Exception e) {
      // Глобальный catch (Exception) на публичном слое запрещён политикой безопасности/границ.
      // Здесь не ловим Exception намеренно: инфраструктурные ошибки — IOException,
      // доменные — свои checked, программные — unchecked не ловим как "успешный" отказ.
      throw new RuntimeException("unexpected exception at import boundary", e);
    }
  }

  public EnrollResult enroll(long studentId, long courseId) {
    try {
      Course course = store.get(courseId);
      if (course == null) {
        return EnrollResult.fail(ErrorCode.HUB_INVALID_ARGUMENT,
            "course " + courseId + " not found", null);
      }
      enrollment.enroll(course, studentId);
      return EnrollResult.ok();
    } catch (EnrollmentRejectedException e) {
      ErrorCode code = switch (e.reasonCode()) {
        case COURSE_CLOSED -> ErrorCode.ENROLL_CLOSED;
        case COURSE_FULL -> ErrorCode.ENROLL_FULL;
        case PREREQUISITE_MISSING -> ErrorCode.ENROLL_PREREQ_MISSING;
        case ALREADY_ENROLLED -> ErrorCode.ENROLL_ALREADY;
      };
      return EnrollResult.fail(code, e.getMessage(), e.getCause());
    } catch (IllegalArgumentException e) {
      return EnrollResult.fail(ErrorCode.HUB_INVALID_ARGUMENT, e.getMessage(), e.getCause());
    } catch (IllegalStateException e) {
      return EnrollResult.fail(ErrorCode.HUB_UNEXPECTED, e.getMessage(), e.getCause());
    } catch (Exception e) {
      throw new RuntimeException("unexpected exception at enroll boundary", e);
    }
  }
}