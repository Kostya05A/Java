package level5;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Course;
import domain.CourseCode;
import domain.EnrollmentService;
import domain.ErrorCode;

@DisplayName("Задание 14. Импорт курсов")
class CourseImporterRollbackTest {

    private EnrollmentService.CourseCatalog catalog;
    private CourseImporter importer;
    private Path workDir;

    @BeforeEach
    void setUp() throws IOException {
        catalog = new EnrollmentService.CourseCatalog();
        importer = new CourseImporter(catalog);

        workDir = Files.createTempDirectory("course-import-test");
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteRecursively(workDir);
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        List<Path> files = new ArrayList<>(Files.walk(root).toList());
        files.sort(Comparator.reverseOrder());
        for (Path file : files) {
            Files.deleteIfExists(file);
        }
    }

    private Path fileWith(String... lines) throws IOException {
        Path file = workDir.resolve("courses-" + lines.length + ".csv");
        Files.writeString(file, String.join("\n", lines), StandardCharsets.UTF_8);
        return file;
    }

    @Nested
    @DisplayName("успешный импорт")
    class Success {

        @Test
        @DisplayName("все записи загружены")
        void allRecordsImported() throws Exception {
            Path file = workDir.resolve("ok.csv");
            Files.writeString(file, """
                    JAVA-101;Основы Java;40
                    WEB-200;Веб-разработка;30
                    DB-300;Базы данных;25
                    """);

            int count = importer.importFrom(file);

            assertAll(
                    () -> assertEquals(3, count),
                    () -> assertEquals(3, catalog.size()),
                    () -> assertNotNull(catalog.find(CourseCode.of("DB-300"))),
                    () -> assertEquals(25, catalog.find(CourseCode.of("DB-300")).completedHours())
            );
        }

        @Test
        @DisplayName("пустые строки пропускаются, а не считаются плохими записями")
        void blankLinesSkipped() throws Exception {
            Path file = workDir.resolve("blank.csv");
            Files.writeString(file, """

                    JAVA-101;Основы Java;40

                    WEB-200;Веб;30

                    """);

            importer.importFrom(file);

            assertEquals(2, catalog.size(), "пустые строки не должны прерывать импорт");
        }

        @Test
        @DisplayName("пустой файл - не отказ, а ноль курсов")
        void emptyFileIsNotAnError() throws Exception {
            Path file = workDir.resolve("empty.csv");
            Files.writeString(file, "");

            int count = importer.importFrom(file);

            assertAll(
                    () -> assertEquals(0, count),
                    () -> assertEquals(0, catalog.size())
            );
        }
    }

    @Nested
    @DisplayName("отказ: плохая запись")
    class MalformedRecord {

        @Test
        @DisplayName("откат: курсы из предыдущих строк не остаются в каталоге")
        void rollbackKeepsCatalogUnchanged() throws Exception {
            Path file = workDir.resolve("bad.csv");
            Files.writeString(file, """
                    JAVA-101;Основы Java;40
                    WEB-200;Веб-разработка;30
                    NOPE;Мусор;20
                    """);

            ImportAbortedException ex = assertThrows(ImportAbortedException.class,
                    () -> importer.importFrom(file));

            assertAll(
                    () -> assertEquals(3, ex.recordNumber(), "номер строки указывает на проблему"),
                    () -> assertEquals("NOPE;Мусор;20", ex.rawRecord()),
                    () -> assertEquals(0, catalog.size(), "ОТКАТ: ни одна запись не применена"),
                    () -> assertEquals(List.of(), List.copyOf(catalog.all()))
            );
        }

        @Test
        @DisplayName("откат не затирает то, что было в каталоге ДО импорта")
        void rollbackRestoresPreviousContent() throws Exception {
            catalog.add(Course.of("OLD-001", "Старый курс", 10));
            Path file = workDir.resolve("bad.csv");
            Files.writeString(file, "JAVA-101;Основы Java;40\nМУСОР;Не разобрать\n");

            assertThrows(ImportAbortedException.class, () -> importer.importFrom(file));

            assertAll(
                    () -> assertEquals(1, catalog.size()),
                    () -> assertNotNull(catalog.find(CourseCode.of("OLD-001")), "прежнее содержимое на месте"),
                    () -> assertEquals(null, catalog.find(CourseCode.of("JAVA-101")), "новое не осталось")
            );
        }

        @ParameterizedTest(name = "запись {0}")
        @ValueSource(strings = {
                "JAVA-101;Основы Java",
                "JAVA-101;Основы Java;40;лишнее",
                "NOPE;Мусор;20",
                "JAVA-102;;20",
                "JAVA-103;Курс;сорок",
                "JAVA-104;Курс;5",
                "JAVA-105;Курс;999"
        })
        @DisplayName("каждая форма плохой записи отвергается")
        void everyMalformedFormRejected(String record) throws Exception {
            Path file = workDir.resolve("one-bad.csv");
            Files.writeString(file, record);

            ImportAbortedException ex = assertThrows(ImportAbortedException.class,
                    () -> importer.importFrom(file));

            assertAll(
                    () -> assertEquals(1, ex.recordNumber()),
                    () -> assertEquals(record, ex.rawRecord()),
                    () -> assertNotNull(ex.message(), "причина отказа заполнена"),
                    () -> assertEquals(0, catalog.size())
            );
        }

        @Test
        @DisplayName("часы не число: причина NumberFormatException сохранена в cause")
        void numberFormatCausePreserved() throws Exception {
            Path file = workDir.resolve("hours.csv");
            Files.writeString(file, "JAVA-101;Основы Java;сорок");

            ImportAbortedException ex = assertThrows(ImportAbortedException.class,
                    () -> importer.importFrom(file));

            assertAll(
                    () -> assertNotNull(ex.getCause(), "cause не потерян при оборачивании"),
                    () -> assertEquals(NumberFormatException.class, ex.getCause().getClass()),
                    () -> assertTrue(ex.message().contains("сорок"), "в причине видно исходное значение")
            );
        }

        @Test
        @DisplayName("код приводится к каноническому виду при импорте")
        void codeNormalizedOnImport() throws Exception {
            Path file = workDir.resolve("lower.csv");
            Files.writeString(file, " java 101 ;Основы Java;40");

            importer.importFrom(file);

            assertNotNull(catalog.find(CourseCode.of("JAVA-101")),
                    "java 101 и JAVA-101 - один и тот же курс");
        }
    }

    @Nested
    @DisplayName("отказ: источник недоступен")
    class SourceUnavailable {

        @Test
        @DisplayName("нет файла -> SourceUnavailableException с SOURCE_UNAVAILABLE")
        void missingFile() {
            Path file = workDir.resolve("нет-такого.csv");

            SourceUnavailableException ex = assertThrows(SourceUnavailableException.class,
                    () -> importer.importFrom(file));

            assertAll(
                    () -> assertEquals(ErrorCode.SOURCE_UNAVAILABLE, ex.errorCode()),
                    () -> assertEquals(file, ex.file(), "оператору нужен путь к файлу"),
                    () -> assertEquals(NoSuchFileException.class, ex.getCause().getClass())
            );
        }

        @Test
        @DisplayName("этот отказ - другого типа, чем плохая запись")
        void differentExceptionTypeThanMalformedRecord() {
            Class<?> unavailable = SourceUnavailableException.class;
            Class<?> malformed = ImportAbortedException.class;

            assertFalse(unavailable.equals(malformed),
                    "два разных отказа должны быть разными типами, "
                            + "иначе вызывающий не сможет отличить «данные плохие» "
                            + "от «система сломалась»");
        }

        @Test
        @DisplayName("источник недоступен: каталог не тронут (откат не требуется)")
        void catalogUntouched() {
            catalog.add(Course.of("OLD-001", "Старый курс", 10));

            assertThrows(SourceUnavailableException.class,
                    () -> importer.importFrom(workDir.resolve("нет.csv")));

            assertAll(
                    () -> assertEquals(1, catalog.size()),
                    () -> assertNotNull(catalog.find(CourseCode.of("OLD-001")))
            );
        }

        @Test
        @DisplayName("директория вместо файла -> тоже SOURCE_UNAVAILABLE")
        void directoryInsteadOfFile() {
            assertThrows(SourceUnavailableException.class,
                    () -> importer.importFrom(workDir));
        }
    }

    @Nested
    @DisplayName("повторный импорт")
    class Repeated {

        @Test
        @DisplayName("повторный импорт тех же данных не создаёт дубликатов")
        void reimportDoesNotDuplicate() throws Exception {
            Path file = workDir.resolve("same.csv");
            Files.writeString(file, "JAVA-101;Основы Java;40\nWEB-200;Веб;30\n");

            importer.importFrom(file);
            importer.importFrom(file);

            assertEquals(2, catalog.size(), "каталог - map по коду, а не список");
        }

        @ParameterizedTest(name = "граница часов {0} проходит импорт")
        @CsvSource({"10", "40"})
        @DisplayName("граничные часы допустимы")
        void boundaryHoursAccepted(int hours) throws Exception {
            Path file = workDir.resolve("boundary.csv");
            Files.writeString(file, "JAVA-101;Основы Java;" + hours);

            importer.importFrom(file);

            assertEquals(hours, catalog.find(CourseCode.of("JAVA-101")).completedHours());
        }
    }
}
