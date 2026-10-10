package domain;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import level1.EnrollmentRejectedException;

public final class EnrollmentService {

    private final CourseCatalog catalog;
    private final TranscriptRepository transcripts;

    public EnrollmentService(CourseCatalog catalog, TranscriptRepository transcripts) {
        this.catalog = catalog;
        this.transcripts = transcripts;
    }

    public EnrollmentResult enroll(long studentId, CourseCode courseCode) {
        Course course = catalog.find(courseCode);
        if (course == null) {
            return EnrollmentResult.rejected(studentId, courseCode, ErrorCode.COURSE_NOT_FOUND);
        }

        if (course.status() == CourseStatus.CLOSED) {
            return EnrollmentResult.rejected(studentId, courseCode, ErrorCode.COURSE_CLOSED);
        }

        if (course.isEnrolled(studentId) || course.isWaitlisted(studentId)) {
            return EnrollmentResult.rejected(studentId, courseCode, ErrorCode.DUPLICATE_APPLICATION);
        }

        for (CourseCode prerequisite : course.prerequisites()) {
            if (!transcripts.hasCompleted(studentId, prerequisite)) {
                return EnrollmentResult.rejected(studentId, courseCode, ErrorCode.PREREQ_MISSING);
            }
        }

        if (!course.hasFreeSeat()) {
            course.addToWaitlist(studentId);
            return EnrollmentResult.waitlisted(studentId, courseCode);
        }

        course.enroll(studentId);
        return EnrollmentResult.accepted(studentId, courseCode);
    }

    public EnrollmentResult enrollOrFail(long studentId, CourseCode courseCode) {
        EnrollmentResult result = enroll(studentId, courseCode);
        return switch (result.status()) {
            case ACCEPTED, WAITLISTED -> result;
            case REJECTED -> throw new EnrollmentRejectedException(
                    studentId, courseIdOf(courseCode), reasonOf(result.reason()));
        };
    }

    private static long courseIdOf(CourseCode courseCode) {
        return courseCode.value().hashCode();
    }

    private static EnrollmentRejectedException.Reason reasonOf(ErrorCode code) {
        return switch (code) {
            case PREREQ_MISSING -> EnrollmentRejectedException.Reason.PREREQ_MISSING;
            case DUPLICATE_APPLICATION -> EnrollmentRejectedException.Reason.DUPLICATE;
            case COURSE_CLOSED -> EnrollmentRejectedException.Reason.COURSE_CLOSED;
            case COURSE_NOT_FOUND -> EnrollmentRejectedException.Reason.COURSE_NOT_FOUND;
            case NO_SEATS -> EnrollmentRejectedException.Reason.NO_SEATS;

            case MALFORMED_RECORD, SOURCE_UNAVAILABLE, OK ->
                    throw new IllegalArgumentException("нет доменной причины для кода " + code);
        };
    }

    public static final class CourseCatalog {
        private final Map<CourseCode, Course> courses = new LinkedHashMap<>();

        public void add(Course course) {
            courses.put(course.code(), course);
        }

        public Course find(CourseCode code) {
            return courses.get(code);
        }

        public Collection<Course> all() {
            return courses.values();
        }

        public int size() {
            return courses.size();
        }

        public Map<CourseCode, Course> snapshot() {
            return Map.copyOf(courses);
        }

        public void restore(Map<CourseCode, Course> state) {
            courses.clear();
            courses.putAll(state);
        }
    }

    public interface TranscriptRepository {
        boolean hasCompleted(long studentId, CourseCode courseCode);
    }
}
