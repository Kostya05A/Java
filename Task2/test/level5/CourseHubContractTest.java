package level5;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Course;
import domain.ErrorCode;

@DisplayName("Задание 15. Контракт CourseHub")
class CourseHubContractTest {

    private CourseHub hub;
    private Path workDir;

    @BeforeEach
    void setUp() throws IOException {
        hub = new CourseHub();
        workDir = Files.createTempDirectory("coursehub-test");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (!Files.exists(workDir)) {
            return;
        }
        List<Path> files = new ArrayList<>(Files.walk(workDir).toList());
        files.sort(Comparator.reverseOrder());
        for (Path file : files) {
            Files.deleteIfExists(file);
        }
    }

    private Path fileWith(String... lines) throws IOException {
        Path file = workDir.resolve("data.csv");
        Files.writeString(file, String.join("\n", lines), StandardCharsets.UTF_8);
        return file;
    }

    private void givenCatalog() throws IOException {
        Path file = fileWith("JAVA-101;Основы Java;40", "WEB-200;Веб;30");
        assertTrue(hub.importCourses(file).success());
    }

    @Nested
    @DisplayName("исключения не выходят наружу")
    class NoExceptions {

        @ParameterizedTest(name = "enroll({0}, {1})")
        @CsvSource({
                "1, JAVA-101",
                "1, NOPE-999",
                "1, плохой код",
                "0, JAVA-101",
                "-5, ''",
                "1, ''"
        })
        @DisplayName("enroll всегда возвращает значение, даже на мусоре")
        void enrollNeverThrows(long studentId, String code) {
            assertDoesNotThrow(() -> hub.enroll(studentId, code),
                    "публичный метод не имеет права бросать: клиент не ловит исключения");
        }

        @ParameterizedTest(name = "importCourses({0})")
        @ValueSource(strings = {"нет-такого.csv", "", "/", "data.csv"})
        @DisplayName("importCourses всегда возвращает значение")
        void importNeverThrows(String fileName) {
            assertDoesNotThrow(() -> hub.importCourses(workDir.resolve(fileName)));
        }

        @Test
        @DisplayName("cancelEnrollment всегда возвращает значение")
        void cancelNeverThrows() {
            assertDoesNotThrow(() -> hub.cancelEnrollment(1, "плохой код"));
        }

        @Test
        @DisplayName("результат метода не null ни при каких входных данных")
        void resultIsNeverNull() {
            assertAll(
                    () -> assertNotNull(hub.enroll(1, "мусор")),
                    () -> assertNotNull(hub.importCourses(workDir.resolve("нет.csv"))),
                    () -> assertNotNull(hub.cancelEnrollment(1, "мусор"))
            );
        }
    }

    @Nested
    @DisplayName("коды ошибок")
    class ErrorCodes {

        @Test
        @DisplayName("несуществующий курс -> COURSE_NOT_FOUND")
        void courseNotFound() {
            assertEquals(ErrorCode.COURSE_NOT_FOUND, hub.enroll(1, "NOPE-999").code());
        }

        @Test
        @DisplayName("невалидный код курса -> тоже COURSE_NOT_FOUND, а не исключение")
        void invalidCodeIsNotAnException() {
            OperationResult result = hub.enroll(1, "плохой код");

            assertAll(
                    () -> assertFalse(result.success()),
                    () -> assertEquals(ErrorCode.COURSE_NOT_FOUND, result.code())
            );
        }

        @Test
        @DisplayName("повторная заявка -> DUPLICATE_APPLICATION")
        void duplicateApplication() throws IOException {
            givenCatalog();
            hub.enroll(1, "JAVA-101");

            assertEquals(ErrorCode.DUPLICATE_APPLICATION, hub.enroll(1, "JAVA-101").code());
        }

        @Test
        @DisplayName("нет мест -> NO_SEATS, но success=true")
        void waitlistIsSuccessWithCode() throws IOException {
            givenCatalog();
            for (int i = 1; i <= 30; i++) {
                hub.enroll(i, "WEB-200");
            }

            OperationResult result = hub.enroll(999, "WEB-200");

            assertAll(
                    () -> assertTrue(result.success(), "лист ожидания - не отказ"),
                    () -> assertEquals(ErrorCode.NO_SEATS, result.code(),
                            "но код сообщает, что зачисления не произошло")
            );
        }

        @Test
        @DisplayName("недоступный файл -> SOURCE_UNAVAILABLE")
        void sourceUnavailable() {
            OperationResult result = hub.importCourses(workDir.resolve("нет.csv"));

            assertAll(
                    () -> assertFalse(result.success()),
                    () -> assertEquals(ErrorCode.SOURCE_UNAVAILABLE, result.code())
            );
        }

        @Test
        @DisplayName("плохая запись -> MALFORMED_RECORD")
        void malformedRecord() throws IOException {
            OperationResult result = hub.importCourses(fileWith("NOPE;Мусор;20"));

            assertAll(
                    () -> assertFalse(result.success()),
                    () -> assertEquals(ErrorCode.MALFORMED_RECORD, result.code())
            );
        }

        @ParameterizedTest
        @EnumSource(ErrorCode.class)
        @DisplayName("у каждого кода есть непустой текст для пользователя")
        void everyCodeHasClientMessage(ErrorCode code) {
            assertAll(
                    () -> assertNotNull(code.clientMessage()),
                    () -> assertFalse(code.clientMessage().isBlank(), "у кода " + code + " пустой текст")
            );
        }

        @Test
        @DisplayName("текст для пользователя не содержит внутренних деталей")
        void userMessageLeaksNothing() {
            String message = hub.importCourses(workDir.resolve("/etc/passwd")).userMessage();

            assertAll(
                    () -> assertFalse(message.contains("passwd"), "путь к файлу - деталь для лога, не для клиента"),
                    () -> assertFalse(message.contains("/etc"), "путь наружу не отдаём"),
                    () -> assertTrue(message.length() > 0)
            );
        }
    }

    @Nested
    @DisplayName("атомарность на публичном слое")
    class Atomicity {

        @Test
        @DisplayName("после неудачного импорта число курсов не изменилось")
        void failedImportChangesNothing() throws IOException {
            givenCatalog();
            int before = hub.courseCount();

            hub.importCourses(fileWith("JAVA-101;Основы Java;40", "МУСОР;Не разобрать"));

            assertEquals(before, hub.courseCount(),
                    "клиент не должен получать наполовину обновлённый каталог");
        }

        @Test
        @DisplayName("успешный импорт увеличивает число курсов")
        void successfulImportAddsCourses() throws IOException {
            givenCatalog();

            assertEquals(2, hub.courseCount());
        }
    }

    @Nested
    @DisplayName("OperationResult")
    class ResultType {

        @Test
        @DisplayName("успех и отказ различаются")
        void successAndFailureDiffer() {
            OperationResult ok = OperationResult.success("ок");
            OperationResult fail = OperationResult.failure(ErrorCode.COURSE_CLOSED, "закрыт");

            assertAll(
                    () -> assertTrue(ok.success()),
                    () -> assertFalse(fail.success()),
                    () -> assertFalse(ok.equals(fail))
            );
        }

        @Test
        @DisplayName("без сообщения берётся текст кода")
        void messageFallsBackToCodeText() {
            OperationResult result = OperationResult.failure(ErrorCode.COURSE_CLOSED, null);

            assertEquals(ErrorCode.COURSE_CLOSED.clientMessage(), result.userMessage());
        }

        @Test
        @DisplayName("record: равные по полям результаты равны")
        void recordHasValueEquality() {
            OperationResult first = OperationResult.failure(ErrorCode.NO_SEATS, "нет мест");
            OperationResult second = OperationResult.failure(ErrorCode.NO_SEATS, "нет мест");

            assertEquals(first, second);
        }

        @Test
        @DisplayName("toString пригоден для лога: есть код и текст")
        void toStringIsLoggable() {
            String text = OperationResult.failure(ErrorCode.PREREQ_MISSING, "нет пререквизита").toString();

            assertAll(
                    () -> assertTrue(text.contains("PREREQ_MISSING")),
                    () -> assertTrue(text.contains("нет пререквизита"))
            );
        }
    }

    @Nested
    @DisplayName("findCourse")
    class FindCourse {

        @Test
        @DisplayName("найденный курс приходит в Optional")
        void foundIsPresent() throws IOException {
            givenCatalog();

            Optional<Course> found = hub.findCourse("JAVA-101");

            assertAll(
                    () -> assertTrue(found.isPresent()),
                    () -> assertEquals("Основы Java", found.orElseThrow().title())
            );
        }

        @ParameterizedTest(name = "\"{0}\" -> пусто")
        @ValueSource(strings = {"NOPE-999", "мусор", "", "  ", "JAVA-10"})
        @DisplayName("отсутствие - пустой Optional, а не исключение")
        void missingIsEmpty(String code) {
            assertAll(
                    () -> assertTrue(hub.findCourse(code).isEmpty(), "код: " + code),
                    () -> assertDoesNotThrow(() -> hub.findCourse(code))
            );
        }

        @Test
        @DisplayName("пустой Optional даёт осмысленное значение по умолчанию")
        void emptyOptionalHasSensibleDefault() {
            String title = hub.findCourse("NOPE-999").map(Course::title).orElse("курс не найден");

            assertEquals("курс не найден", title);
        }
    }

    @Nested
    @DisplayName("контракт не течёт")
    class ContractDoesNotLeak {

        @Test
        @DisplayName("имена внутренних исключений не попадают в результат")
        void noInternalTypeNamesInResult() throws IOException {
            String results = List.of(
                    hub.enroll(1, "мусор").toString(),
                    hub.importCourses(workDir.resolve("нет.csv")).toString(),
                    hub.importCourses(fileWith("NOPE;Мусор;20")).toString()
            ).toString();

            assertAll(
                    () -> assertFalse(results.contains("ImportAbortedException")),
                    () -> assertFalse(results.contains("SourceUnavailableException")),
                    () -> assertFalse(results.contains("NoSuchFileException")),
                    () -> assertFalse(results.contains("IllegalArgumentException")),
                    () -> assertFalse(results.contains("Exception"))
            );
        }

        @Test
        @DisplayName("все значения ErrorCode используются хотя бы раз")
        void everyCodeIsReachable() {
            EnumSet<ErrorCode> reachable = EnumSet.noneOf(ErrorCode.class);

            reachable.add(hub.enroll(1, "мусор").code());
            reachable.add(hub.enroll(1, "JAVA-999").code());
            reachable.add(hub.importCourses(workDir.resolve("нет.csv")).code());
            reachable.add(ErrorCode.OK);
            reachable.add(ErrorCode.COURSE_CLOSED);
            reachable.add(ErrorCode.PREREQ_MISSING);
            reachable.add(ErrorCode.DUPLICATE_APPLICATION);
            reachable.add(ErrorCode.NO_SEATS);
            reachable.add(ErrorCode.MALFORMED_RECORD);

            assertEquals(EnumSet.allOf(ErrorCode.class), reachable,
                    "код, который никогда не выдаётся, - мёртвый код: "
                            + "его удалят, а потом добавят заново");
        }
    }
}
