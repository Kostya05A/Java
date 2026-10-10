package level5;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Course;
import domain.CourseCode;
import domain.EnrollmentResult;
import domain.EnrollmentService;
import domain.ErrorCode;
import level1.EnrollmentRejectedException;

@DisplayName("Задание 13. Таблица решений")
class EnrollmentDecisionTableTest {

    private static final long STUDENT = 42L;
    private static final CourseCode JAVA = CourseCode.of("JAVA-101");
    private static final CourseCode WEB = CourseCode.of("WEB-200");

    private EnrollmentService.CourseCatalog catalog;
    private EnrollmentService.TranscriptRepository transcripts;
    private EnrollmentService service;

    private void givenOpenCourse(CourseCode code, int capacity) {
        catalog.add(new Course(code, "Курс " + code, capacity));
    }

    private void givenClosedCourse(CourseCode code, int capacity) {
        Course course = new Course(code, "Курс " + code, capacity);
        course.close();
        catalog.add(course);
    }

    private static EnrollmentService.TranscriptRepository transcriptsOf(String... completed) {
        Set<CourseCode> passed = new HashSet<>();
        for (String code : completed) {
            passed.add(CourseCode.of(code));
        }
        return (studentId, courseCode) -> passed.contains(courseCode);
    }

    @Nested
    @DisplayName("успех")
    class Success {

        @Test
        @DisplayName("свободные места -> ACCEPTED")
        void acceptedWhenFreeSeat() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenOpenCourse(JAVA, 10);

            EnrollmentResult result = service.enroll(STUDENT, JAVA);

            assertAll(
                    () -> assertEquals(EnrollmentResult.Status.ACCEPTED, result.status()),
                    () -> assertTrue(result.isAccepted()),
                    () -> assertEquals(null, result.reason(), "у успеха нет причины отказа"),
                    () -> assertTrue(catalog.find(JAVA).isEnrolled(STUDENT))
            );
        }

        @Test
        @DisplayName("нет мест -> WAITLISTED, а не отказ")
        void waitlistedWhenNoSeats() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenOpenCourse(JAVA, 1);
            service.enroll(1L, JAVA);

            EnrollmentResult result = service.enroll(STUDENT, JAVA);

            assertAll(
                    () -> assertEquals(EnrollmentResult.Status.WAITLISTED, result.status()),
                    () -> assertEquals(ErrorCode.NO_SEATS, result.reason()),
                    () -> assertFalse(catalog.find(JAVA).isEnrolled(STUDENT), "в лист ожидания, не зачислён"),
                    () -> assertTrue(catalog.find(JAVA).waitlist().contains(STUDENT), "добавлен в лист ожидания")
            );
        }
    }

    @Nested
    @DisplayName("отказы")
    class Rejections {

        @Test
        @DisplayName("курса нет -> COURSE_NOT_FOUND")
        void courseNotFound() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);

            EnrollmentResult result = service.enroll(STUDENT, JAVA);

            assertAll(
                    () -> assertEquals(EnrollmentResult.Status.REJECTED, result.status()),
                    () -> assertEquals(ErrorCode.COURSE_NOT_FOUND, result.reason()),
                    () -> assertEquals(STUDENT, result.studentId(), "в отказе видно, о ком речь"),
                    () -> assertEquals(JAVA, result.courseCode())
            );
        }

        @Test
        @DisplayName("курс закрыт -> COURSE_CLOSED")
        void courseClosed() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenClosedCourse(JAVA, 10);

            EnrollmentResult result = service.enroll(STUDENT, JAVA);

            assertAll(
                    () -> assertEquals(EnrollmentResult.Status.REJECTED, result.status()),
                    () -> assertEquals(ErrorCode.COURSE_CLOSED, result.reason()),
                    () -> assertFalse(catalog.find(JAVA).isEnrolled(STUDENT))
            );
        }

        @Test
        @DisplayName("курс закрыт, но есть места и нет пререквизитов -> всё равно COURSE_CLOSED")
        void closedCourseWinsOverEverythingElse() {
            catalog = new EnrollmentService.CourseCatalog();

            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenClosedCourse(JAVA, 10);

            EnrollmentResult result = service.enroll(STUDENT, JAVA);

            assertEquals(ErrorCode.COURSE_CLOSED, result.reason(),
                    "порядок проверок: закрытый курс отвергает раньше остальных условий");
        }

        @Test
        @DisplayName("уже зачислен -> DUPLICATE_APPLICATION")
        void duplicateApplication() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenOpenCourse(JAVA, 10);
            service.enroll(STUDENT, JAVA);

            EnrollmentResult result = service.enroll(STUDENT, JAVA);

            assertAll(
                    () -> assertEquals(EnrollmentResult.Status.REJECTED, result.status()),
                    () -> assertEquals(ErrorCode.DUPLICATE_APPLICATION, result.reason()),
                    () -> assertEquals(1, catalog.find(JAVA).enrolledCount(), "повторная заявка не создала запись")
            );
        }

        @Test
        @DisplayName("дубликат важнее листа ожидания: повторно в лист не попадаем")
        void duplicateBeatsWaitlist() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenOpenCourse(JAVA, 1);
            service.enroll(1L, JAVA);
            service.enroll(STUDENT, JAVA);

            EnrollmentResult result = service.enroll(STUDENT, JAVA);

            assertAll(
                    () -> assertEquals(ErrorCode.DUPLICATE_APPLICATION, result.reason()),
                    () -> assertEquals(1, catalog.find(JAVA).waitlist().size(), "в листе ожидания не задвоился")
            );
        }

        @Test
        @DisplayName("пререквизит не пройден -> PREREQ_MISSING")
        void prerequisiteMissing() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            Course web = new Course(WEB, "Веб", 10);
            web.addPrerequisite(JAVA);
            catalog.add(web);

            EnrollmentResult result = service.enroll(STUDENT, WEB);

            assertAll(
                    () -> assertEquals(EnrollmentResult.Status.REJECTED, result.status()),
                    () -> assertEquals(ErrorCode.PREREQ_MISSING, result.reason()),
                    () -> assertFalse(web.isEnrolled(STUDENT)),
                    () -> assertTrue(web.waitlist().isEmpty(), "в лист ожидания при отказе не попадаем")
            );
        }

        @Test
        @DisplayName("пререквизит пройден -> ACCEPTED")
        void prerequisiteSatisfied() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf("JAVA-101");
            service = new EnrollmentService(catalog, transcripts);
            Course web = new Course(WEB, "Веб", 10);
            web.addPrerequisite(JAVA);
            catalog.add(web);

            EnrollmentResult result = service.enroll(STUDENT, WEB);

            assertEquals(EnrollmentResult.Status.ACCEPTED, result.status());
        }

        @Test
        @DisplayName("пререквизит важнее мест: при его отсутствии до листа ожидания не доходим")
        void prerequisiteBeatsWaitlist() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            Course web = new Course(WEB, "Веб", 1);
            web.addPrerequisite(JAVA);
            catalog.add(web);
            Course java = new Course(JAVA, "Java", 1);
            catalog.add(java);
            service.enroll(1L, JAVA);

            EnrollmentResult result = service.enroll(STUDENT, WEB);

            assertEquals(ErrorCode.PREREQ_MISSING, result.reason(),
                    "сначала пререквизит, потом места");
        }
    }

    static Stream<Arguments> decisionTable() {
        return Stream.of(

                Arguments.of(false, true, false, true, true,
                        EnrollmentResult.Status.REJECTED, ErrorCode.COURSE_NOT_FOUND),
                Arguments.of(true, false, false, true, true,
                        EnrollmentResult.Status.REJECTED, ErrorCode.COURSE_CLOSED),
                Arguments.of(true, true, true, true, true,
                        EnrollmentResult.Status.REJECTED, ErrorCode.DUPLICATE_APPLICATION),
                Arguments.of(true, true, false, false, true,
                        EnrollmentResult.Status.REJECTED, ErrorCode.PREREQ_MISSING),
                Arguments.of(true, true, false, true, true,
                        EnrollmentResult.Status.ACCEPTED, null),
                Arguments.of(true, true, false, true, false,
                        EnrollmentResult.Status.WAITLISTED, ErrorCode.NO_SEATS)
        );
    }

    @ParameterizedTest(name = "[{index}] есть={0} открыт={1} дубликат={2} пререквизит={3} места={4} -> {5} {6}")
    @MethodSource("decisionTable")
    @DisplayName("полная таблица решений")
    void decisionTableIsExhaustive(
            boolean courseExists,
            boolean open,
            boolean duplicate,
            boolean prerequisitesMet,
            boolean hasFreeSeat,
            EnrollmentResult.Status expectedStatus,
            ErrorCode expectedReason) {

        catalog = new EnrollmentService.CourseCatalog();

        service = new EnrollmentService(catalog, prerequisitesMet ? transcriptsOf("JAVA-101") : transcriptsOf());

        if (courseExists) {

            Course course = new Course(WEB, "Веб", 1);
            if (!open) {
                course.close();
            }
            if (!prerequisitesMet) {
                course.addPrerequisite(JAVA);
            }
            catalog.add(course);

            if (duplicate) {
                course.enroll(STUDENT);
            } else if (!hasFreeSeat) {
                course.enroll(999L);
            }
        }

        EnrollmentResult result = service.enroll(STUDENT, WEB);

        assertAll(
                () -> assertEquals(expectedStatus, result.status()),
                () -> assertEquals(expectedReason, result.reason())
        );
    }

    @Nested
    @DisplayName("enrollOrFail: тот же отказ, но исключение")
    class EnrollOrFail {

        @Test
        @DisplayName("успех возвращается как есть")
        void successIsReturned() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenOpenCourse(JAVA, 10);

            EnrollmentResult result = service.enrollOrFail(STUDENT, JAVA);

            assertTrue(result.isAccepted());
        }

        @Test
        @DisplayName("лист ожидания - тоже успех, исключения нет")
        void waitlistIsNotAFailure() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenOpenCourse(JAVA, 1);
            service.enroll(1L, JAVA);

            EnrollmentResult result = service.enrollOrFail(STUDENT, JAVA);

            assertEquals(EnrollmentResult.Status.WAITLISTED, result.status());
        }

        @ParameterizedTest(name = "код курса {0} -> {1}")
        @CsvSource({
                "NOPE-999, COURSE_NOT_FOUND",
                "JAVA-102, COURSE_CLOSED"
        })
        @DisplayName("доменный отказ превращается в EnrollmentRejectedException")
        void rejectionBecomesException(String code, EnrollmentRejectedException.Reason expected) {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenClosedCourse(CourseCode.of("JAVA-102"), 10);

            EnrollmentRejectedException ex = assertThrows(EnrollmentRejectedException.class,
                    () -> service.enrollOrFail(STUDENT, CourseCode.of(code)));

            assertAll(
                    () -> assertEquals(STUDENT, ex.studentId()),
                    () -> assertEquals(expected, ex.reason(),
                            "любой отказ из таблицы решений должен иметь причину; "
                                    + "раньше COURSE_NOT_FOUND тут падал в IllegalArgumentException"),
                    () -> assertTrue(ex.getMessage().contains(expected.name()))
            );
        }

        @Test
        @DisplayName("причина есть в тексте исключения")
        void exceptionCarriesDomainReason() {
            catalog = new EnrollmentService.CourseCatalog();
            transcripts = transcriptsOf();
            service = new EnrollmentService(catalog, transcripts);
            givenClosedCourse(JAVA, 10);

            EnrollmentRejectedException ex = assertThrows(EnrollmentRejectedException.class,
                    () -> service.enrollOrFail(STUDENT, JAVA));

            assertAll(
                    () -> assertEquals(EnrollmentRejectedException.Reason.COURSE_CLOSED, ex.reason()),
                    () -> assertTrue(ex.getMessage().contains("COURSE_CLOSED"), "причина есть в тексте")
            );
        }
    }

    @Test
    @DisplayName("любой отказ несёт studentId и courseCode")
    void rejectionCarriesContext() {
        catalog = new EnrollmentService.CourseCatalog();
        transcripts = transcriptsOf();
        service = new EnrollmentService(catalog, transcripts);

        EnrollmentResult result = service.enroll(777L, CourseCode.of("WEB-200"));

        assertAll(
                () -> assertEquals(777L, result.studentId()),
                () -> assertEquals(CourseCode.of("WEB-200"), result.courseCode()),
                () -> assertTrue(result.toString().contains("777"), "toString пригоден для лога")
        );
    }

    @Test
    @DisplayName("в успешном результате reason == null")
    void successHasNoReason() {
        catalog = new EnrollmentService.CourseCatalog();
        transcripts = transcriptsOf();
        service = new EnrollmentService(catalog, transcripts);
        givenOpenCourse(JAVA, 10);

        EnrollmentResult result = service.enroll(STUDENT, JAVA);

        assertEquals(null, result.reason());
    }

    @Test
    @DisplayName("toString отказа содержит причину")
    void rejectionToStringContainsReason() {
        catalog = new EnrollmentService.CourseCatalog();
        transcripts = transcriptsOf();
        service = new EnrollmentService(catalog, transcripts);

        EnrollmentResult result = service.enroll(STUDENT, JAVA);

        assertTrue(result.toString().contains("COURSE_NOT_FOUND"),
                "лог должен содержать код, иначе разбираться нечем");
    }
}
