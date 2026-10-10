package level4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Course;
import domain.CourseCode;
import domain.CourseStatus;

@DisplayName("Хрупкие тесты: было / стало")
class FragileTestsFixedTest {

    private static final AtomicInteger GLOBAL_COUNTER = new AtomicInteger();

    @Test
    @Disabled("демонстрация хрупкости: падает в зависимости от порядка выполнения")
    @DisplayName("БЫЛО: static state - тест ждёт, что счётчик пуст")
    void wasBrokenByStaticState() {
        assertEquals(0, GLOBAL_COUNTER.get(),
                "падёт, если до этого отработал другой тест, трогающий счётчик");

        GLOBAL_COUNTER.incrementAndGet();

        assertEquals(1, GLOBAL_COUNTER.get());
    }

    @Test
    @DisplayName("СТАЛО: состояние создаётся в каждом тесте")
    void isIndependentOfOtherTests() {
        AtomicInteger localCounter = new AtomicInteger();

        localCounter.incrementAndGet();
        localCounter.incrementAndGet();

        assertEquals(2, localCounter.get(), "локальный счётчик не зависит ни от кого снаружи");
    }

    private Course sharedCourse;

    @BeforeEach
    void resetSharedCourse() {

        sharedCourse = Course.of("JAVA-101", "Основы Java", 10);
    }

    @Test
    @DisplayName("СТАЛО: тест сам создаёт нужное состояние")
    void isSelfContained() {

        sharedCourse.completeHours(20);

        assertAll(
                () -> assertTrue(sharedCourse.isCompleted()),
                () -> assertEquals(20, sharedCourse.completedHours())
        );
    }

    @Nested
    @DisplayName("хрупкие варианты (отключены)")
    class FragileVariants {

        @Test
        @Disabled("демонстрация хрупкости: тест падает в зависимости от порядка выполнения")
        @DisplayName("БЫЛО: тест опирается на состояние, оставленное другим тестом")
        void wasBrokenByOrderDependency() {

            assertTrue(sharedCourse.isCompleted(),
                    "падёт, если до этого не отработал соседний тест");

            assertEquals(20, sharedCourse.completedHours(),
                    "падёт, если предыдущий тест не вызвал completeHours(20)");
        }
    }

    @Test
    @DisplayName("СТАЛО: @TempDir даёт путь, не зависящий от директории запуска")
    void isIndependentOfWorkingDirectory(@TempDir Path tempDir) throws IOException {

        Path file = tempDir.resolve("nested").resolve("courses.csv");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "JAVA-101;Основы Java;40", StandardCharsets.UTF_8);

        String content = Files.readString(file);

        assertEquals("JAVA-101;Основы Java;40", content);
    }

    @Test
    @DisplayName("СТАЛО: фикстура из classpath не зависит от директории запуска")
    void readsFixtureFromClasspath() throws IOException {
        try (var stream = FragileTestsFixedTest.class
                .getResourceAsStream("/testdata/courses.csv")) {

            assertNotNull(stream, "фикстура /testdata/courses.csv должна быть в classpath");

            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(content.contains("JAVA-101"),
                    "фикстура должна содержать тестовый курс");
        }
    }

    @Test
    @DisplayName("БЫЛО: сравнение полного текста toString")
    void wasBrokenByToStringComparison() {
        Course course = Course.of("JAVA-101", "Основы Java", 30);
        course.completeHours(20);

        String expected = "Course[JAVA-101 'Основы Java', status=OPEN, capacity=30, enrolled=0, completedHours=20]";
        assertEquals(expected, course.toString(),
                "упадёт при любом изменении Course.toString()");
    }

    @Test
    @DisplayName("СТАЛО: проверяем свойства, а не их склейку в toString")
    void checksPropertiesNotString() {
        Course course = Course.of("JAVA-101", "Основы Java", 30);
        course.completeHours(20);

        assertAll(
                () -> assertEquals(CourseCode.of("JAVA-101"), course.code()),
                () -> assertEquals("Основы Java", course.title()),
                () -> assertEquals(30, course.capacity()),
                () -> assertEquals(20, course.completedHours()),
                () -> assertEquals(CourseStatus.OPEN, course.status())
        );
    }

    @Test
    @DisplayName("СТАЛО (частично): если toString важен, проверяют ключевые фрагменты")
    void checksToStringFragmentsOnly() {
        Course course = Course.of("JAVA-101", "Основы Java", 30);
        course.completeHours(20);

        String text = course.toString();

        assertAll(
                () -> assertTrue(text.contains("JAVA-101"), "в логе должен быть виден код курса"),
                () -> assertTrue(text.contains("Основы Java"), "в логе должно быть видно название")
        );
    }

    @Test
    @DisplayName("проверка: два одинаковых объекта дают одинаковый результат независимо от прошлого")
    void pureFunctionHasNoMemory() {

        Course first = Course.of("JAVA-101", "Основы Java", 30);
        first.completeHours(20);

        Course second = Course.of("JAVA-101", "Основы Java", 30);
        second.completeHours(20);

        assertAll(
                () -> assertEquals(first.completedHours(), second.completedHours()),
                () -> assertEquals(first.code(), second.code()),
                () -> assertEquals(first.toString(), second.toString(),
                        "одинаковые состояния дают одинаковый текст")
        );
    }

    @Test
    @DisplayName("справочно: одиночный запуск метода")
    void runsAlone() {
        Course course = Course.of("JAVA-101", "Основы Java", 10);
        course.completeHours(15);
        assertEquals(15, course.completedHours());
    }

}
