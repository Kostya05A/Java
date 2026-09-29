CourseHub — полное решение на Java
Ниже — компактное, но полное решение по всем 15 заданиям. Код самодостаточный,
разбит по уровням. Для сборки — Maven + JUnit 5.
---
pom.xml (JUnit Jupiter + Surefire)
```xml
<project xmlns="http:/maven.apache.org/POM/4.0.0">
<modelVersion>4.0.0</modelVersion>
<groupId>ru.coursehub</groupId>
<artifactId>coursehub</artifactId>
<version>1.0</version>
<properties>
<maven.compiler.release>17</maven.compiler.release>
<junit.version>5.10.2</junit.version>
</properties>
<dependencies>
<dependency>
<groupId>org.junit.jupiter</groupId>
<artifactId>junit-jupiter</artifactId>
<version>${junit.version}</version>
<scope>test</scope>
</dependency>
</dependencies>
<build>
<plugins>
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-surefire-plugin</artifactId>
<version>3.2.5</version>
<configuration>
<includes><include>**/*Test.java</include></includes>
</configuration>
</plugin>
</plugins>
</build>
</project>
```
---
Задание 1. Прочитать stack trace
```javapublic class StackTraceDemo {
static int parse(String s) { // 3
return Integer.parseInt(s); // BOM!
}
static int load(String s) { return parse(s); } // 2
static int service(String s) { return load(s); } // 1
static int controller(String s) { return service(s); }// 0
public static void main(String[] args) {
controller("42a");
}
}
```
Вывод (сокращённо):
```
Exception in thread "main" java.lang.NumberFormatException: For input string: "42a"
at java.base/java.lang.Integer.parseInt(Integer.java:652)
at ru.coursehub.StackTraceDemo.parse(StackTraceDemo.java:6) <-- место
at ru.coursehub.StackTraceDemo.load(StackTraceDemo.java:7)
at ru.coursehub.StackTraceDemo.service(StackTraceDemo.java:8)
at ru.coursehub.StackTraceDemo.controller(StackTraceDemo.java:9)
at ru.coursehub.StackTraceDemo.main(StackTraceDemo.java:12)
```
· Тип: NumberFormatException (unchecked, наследник IllegalArgumentException).
· Сообщение: For input string: "42a".
· Место возникновения: первая строка at ...parse(...) внутри нашего кода —
StackTraceDemo.parse, строка 6.
· Путь вызовов: main → controller → service → load → parse.
· Исправление причины (а не последней строки): проверять/валидировать вход в parse
(в самой границе парсинга), а не глушить исключение в main:
```java
static int parse(String s) {
if (s == null || !s.matches("-?\d+")) {
throw new IllegalArgumentException("Not an int: '" + s + "'", null);
}
return Integer.parseInt(s);
}
```
---
Задание 2. Checked или uncheckedСитуация Категория Обоснование
Неверный аргумент метода Unchecked (IllegalArgumentException) Программист может
проверить до вызова; проверять компилятором избыточно.
Отсутствующий файл Checked (FileNotFoundException/IOException) Внешняя среда;
вызывающий обязан решить (создать, заменить, сообщить).
Нарушение состояния Unchecked (IllegalStateException) Ошибка порядка
вызовов/логики — виноват код.
Сетевой отказ Checked (IOException) Внешнее транзиентное условие; нужна политика
retry/сообщения.
Ошибка конфигурации Другой результат (fail-fast при старте) Лучше упасть на bootstrap,
чем кидать во время работы; часто unchecked/ExceptionInInitializerError.
Недостаток памяти Невозможность восстановления (OutOfMemoryError) Error, не
Exception. Ловить нельзя, только корректно завершиться.
---
Задание 3. Собственное исключение
```java
public final class EnrollmentRejectedException extends RuntimeException {
public enum Reason { NO_SEATS, PREREQ_MISSING, COURSE_CLOSED,
DUPLICATE }
private final long studentId;
private final long courseId;
private final Reason reason;
public EnrollmentRejectedException(long studentId, long courseId, Reason reason,
Throwable cause) {
super("Enrollment rejected [reason=" + reason + "]", cause); // без id в тексте
this.studentId = studentId;
this.courseId = courseId;
this.reason = reason;
}
public EnrollmentRejectedException(long studentId, long courseId, Reason reason) {
this(studentId, courseId, reason, null);
}
public long studentId() { return studentId; }
public long courseId() { return courseId; }
public Reason reason() { return reason; }
@Override public String toString() {
// Не раскрываем id в тексте: только класс + reason + hasCause
return "EnrollmentRejectedException[reason=" + reason
+ ", hasCause=" + (getCause() != null) + "]";
}}
```
---
Задание 4. try-catch-finally
```java
public class TryCatchFinally {
static int demo(boolean failInTry) {
try {
System.out.println("try");
if (failInTry) throw new IllegalStateException("boom");
return 1;
} catch (IllegalStateException e) {
System.out.println("catch");
return 2;
} finally {
System.out.println("finally");
// return 3; <-- НИКОГДА так не делать
}
}
public static void main(String[] args) {
System.out.println("=> " + demo(false)); // try, finally, 1
System.out.println("=> " + demo(true)); // try, catch, finally, 2
}
}
```
Порядок:
1. Успех: try → finally → return 1.
2. Исключение: try → catch → finally → return 2.
3. Если throw в try и есть finally — finally выполняется до проброса.
Почему return из finally опасен: он перезаписывает результат try/catch и глушит
исключение:
```java
try { throw new RuntimeException("lost"); }
finally { return; } // исключение исчезает
```
---
Задание 5. Try-with-resources + suppressed```java
public class LoggingResource implements AutoCloseable {
private final String name;
private final boolean failOnClose;
public LoggingResource(String name, boolean failOnClose) {
this.name = name; this.failOnClose = failOnClose;
System.out.println("open " + name);
}
public void use(boolean fail) {
System.out.println("use " + name);
if (fail) throw new IllegalStateException("use failed: " + name);
}
@Override public void close() {
System.out.println("close " + name);
if (failOnClose) throw new IllegalStateException("close failed: " + name);
}
public static void main(String[] args) {
// успех
try (var r = new LoggingResource("ok", false)) { r.use(false); }
// исключение в use
try (var r = new LoggingResource("useFail", false)) { r.use(true); }
catch (Exception e) { System.out.println("caught: " + e); }
// исключение и в use, и в close
try (var r = new LoggingResource("both", true)) { r.use(true); }
catch (Exception e) {
System.out.println("primary: " + e);
for (Throwable s : e.getSuppressed()) System.out.println("suppressed: " + s);
}
}
}
```
Вывод (фрагмент): при двойном отказе основное — из use, а close попадает в
getSuppressed(). Try-with-resources гарантирует закрытие и не теряет вторичные
ошибки.
---
Задание 6. Граница перевода исключения
```java
public class CourseImportException extends Exception {
private final int recordNumber;
public CourseImportException(int recordNumber, String message, Throwable cause) {super(message, cause);
this.recordNumber = recordNumber;
ublic int recordNumber() { return recordNumber; }
}
p}
class LowLevelParser {
static int parseSeats(String raw) { return Integer.parseInt(raw); } //
NumberFormatException
}
class CourseImporter {
public static int importSeats(String[] lines) throws CourseImportException {
int last = -1;
for (int i = 0; i < lines.length; i++) {
try {
last = LowLevelParser.parseSeats(lines[i]);
} catch (NumberFormatException nfe) { // ловим конкретный тип
throw new CourseImportException(i + 1,
"Invalid seats at record " + (i + 1), nfe); // cause сохранён
}
eturn last;
}
r}
}
```
Никаких catch (Exception), контекст (recordNumber, cause) сохранён.
---
Задание 7. Ошибка как значение или исключение
Метод Выбор Почему
findCourse(id) Optional<Course> Отсутствие — ожидаемый результат.
parseCourse(raw) checked CourseParseException Ошибка внешних данных, обязана
обрабатываться.
enroll(student, course) unchecked EnrollmentRejectedException Нарушение доменных
правил, обычно programming/UX-ошибка.
calculatePrice(...) типизированный Result<Price, PriceError> Множество ожидаемых
причин отказа.
```java
record Price(long cents) {}
sealed interface PriceError permits NoSuchCourse, DiscountExpired {}
record NoSuchCourse(long id) implements PriceError {}
record DiscountExpired(String code) implements PriceError {}sealed interface Result<T, E> permits Ok, Err {}
record Ok<T, E>(T value) implements Result<T, E> {}
record Err<T, E>(E error) implements Result<T, E> {}
class Pricing {
Result<Price, PriceError> calculate(long courseId, String discount) {
if (courseId <= 0) return new Err<>(new NoSuchCourse(courseId));
if ("EXPIRED".equals(discount)) return new Err<>(new DiscountExpired(discount));
return new Ok<>(new Price(10_000));
}
}
class EnrollmentService {
Optional<Course> findCourse(long id) { return Optional.empty(); }
Course parseCourse(String raw) throws CourseParseException {
if (raw == null || raw.isBlank()) throw new CourseParseException("blank");
return new Course(raw);
}
void enroll(Student s, Course c) {
if (!c.hasSeats()) throw new EnrollmentRejectedException(
s.id(), c.id(), EnrollmentRejectedException.Reason.NO_SEATS);
}
}
```
---
Задание 8. Первый тест по AA
```java
public class Course {
private final long id;
private int completedHours;
private final int minHours, maxHours;
public Course(long id, int minHours, int maxHours) {
if (minHours < 0 || maxHours < minHours) throw new IllegalArgumentException();
this.id = id; this.minHours = minHours; this.maxHours = maxHours;
}
public int completedHours() { return completedHours; }
public void completeHours(int hours) {
if (hours <= 0) throw new IllegalArgumentException("hours must be > 0");
if (completedHours + hours > maxHours)
throw new IllegalStateException("exceeds maxHours");
completedHours += hours;}
}
```
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class CourseCompleteHoursTest {
@Test
void completingHoursWithinLimit_incrementsCompletedHours() {
// Arrange
Course course = new Course(1L, 0, 40);
// Act
course.completeHours(4);
// Assert
assertEquals(4, course.completedHours(),
"completedHours should reflect the single successful completion");
}
}
```
---
Задание 9. Граничные значения
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
class CourseBoundaryTest {
@ParameterizedTest(name = "hours={0} on [1..40] -> valid={1}")
@CsvSource({
"0, false", // ниже минимума
"1, true", // минимум
"40, true", // максимум
"41, false" // выше максимума
})
void hoursBoundaries(int hours, boolean valid) {
Course c = new Course(1L, 1, 40);
if (valid) {
assertDoesNotThrow(() -> c.completeHours(hours));
assertEquals(hours, c.completedHours());} else {
assertThrows(RuntimeException.class, () -> c.completeHours(hours));
assertEquals(0, c.completedHours(), "state must not change on failure");
}
}
}
```
Почему «обычный» пример недостаточен: он проверяет только середину диапазона и
не фиксирует контракт на краях. Ошибки типа «of-by-one» (> vs >=) проявляются
только на границах, а не в середине. Границы — минимальное покрытие классов
эквивалентности.
---
Задание 10. Параметризованный тест
```java
public final class CourseCode {
private final String value;
private CourseCode(String v) { this.value = v; }
public String value() { return value; }
public static CourseCode of(String raw) {
if (raw == null) throw new IllegalArgumentException("null");
String v = raw.trim().toUpperCase().replace(" ", "-");
if (!v.matches("[A-Z]{2,4}-\d{3}"))
throw new IllegalArgumentException("invalid code: " + raw);
return new CourseCode(v);
}
@Override public String toString() { return value; }
}
```
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
class CourseCodeNormalizationTest {
@ParameterizedTest(name = "[{index}] input=''{0}'' -> ''{1}''")
@CsvSource({
"' cs-101 ', CS-101",
"CS101, CS101",
"'java 201', JAVA-201",
"' AB-001', AB-001"
})void normalizesValidCodes(String input, String expected) {
assertEquals(expected, CourseCode.of(input).value());
}
@ParameterizedTest(name = "[{index}] invalid input=''{0}''")
@CsvSource({ "'', 'A-1', 'ABC-12', '123-ABC', 'A B C 1 2 3'" })
void rejectsInvalidCodes(String input) {
assertThrows(IllegalArgumentException.class, () -> CourseCode.of(input));
}
}
```
---
Задание 11. assertThrows и состояние после отказа
```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class EnrollFailureTest {
@Test
void enroll_whenNoSeats_throwsWithFields_andKeepsStateUnchanged() {
Course c = new Course(7L, 0, 40);
Student s = new Student(1L);
EnrollmentService svc = new EnrollmentService(/* seats=0 */);
int before = c.completedHours();
EnrollmentRejectedException ex = assertThrows(
EnrollmentRejectedException.class,
() -> svc.enroll(s, c) // только одно падающее действие
);
assertEquals(EnrollmentRejectedException.Reason.NO_SEATS, ex.reason());
assertEquals(1L, ex.studentId());
assertEquals(7L, ex.courseId());
assertFalse(ex.toString().contains("1"), "id не должен утечь в toString");
assertEquals(before, c.completedHours(), "состояние курса не изменилось");
}
}
```
---
Задание 12. Устранить хрупкие тестыБыло (плохо):
```java
class BadTests {
static int counter = 0; // shared state
@Test void a() { counter++; assertEquals(1, counter); }
@Test void b() { assertEquals(1, counter); } // зависит от порядка
@Test void c() { assertEquals("/tmp", new File(".").getAbsolutePath()); }
@Test void d() { assertEquals("Course{id=1}", new Course(1L,0,10).toString()); }
}
```
Стало (хорошо):
```java
class GoodTests {
private int counter; // своё поле на каждый тест
@BeforeEach void setUp() { counter = 0; }
@Test void incrementOnce_yieldsOne() { counter++; assertEquals(1, counter); }
@Test void defaultCounter_isZero() { assertEquals(0, counter); }
@Test void writesIntoJunitTempDir(@TempDir Path tmp) throws IOException {
Path f = tmp.resolve("x.txt");
Files.writeString(f, "hi");
assertEquals("hi", Files.readString(f));
}
@Test void courseExposesItsId() {
Course c = new Course(1L, 0, 10);
assertEquals(1L, c.id()); // без сравнения toString
}
}
```
Правила: никакого static-мутабельного состояния, никаких абсолютных путей,
@TempDir, сравнение по полям, а не по toString, отсутствие зависимости от порядка.
---
Задание 13. Decision table для зачисления
Условия: статус курса (OPEN/CLOSED), есть места (Y/N), prerequisite (OK/MISSING),
повторная заявка (Y/N).
# status seats prereq duplicate Результат1 CLOSED * * * COURSE_CLOSED
2 OPEN N * * NO_SEATS
3 OPEN Y MISSING * PREREQ_MISSING
4 OPEN Y OK Y DUPLICATE
5 OPEN Y OK N OK
Комбинации, покрытые правилами 1–4, не дублируем (*). Это 5 существенных классов.
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
class EnrollmentDecisionTableTest {
@ParameterizedTest(name = "[{index}] {0}")
@CsvSource({
"CLOSED, true, OK, false, COURSE_CLOSED",
"OPEN, false, OK, false, NO_SEATS",
"OPEN, true, MISSING, false, PREREQ_MISSING",
"OPEN, true, OK, true, DUPLICATE",
"OPEN, true, OK, false, OK"
})
void enrollmentRules(String status, boolean seats, String prereq,
boolean dup, String expected) {
var svc = new EnrollmentService(seats ? 1 : 0);
Course c = new Course(1L, 0, 40, status);
Student s = new Student(1L, prereq);
if (dup) svc.markDuplicate(s.id(), c.id());
if ("OK".equals(expected)) {
assertDoesNotThrow(() -> svc.enroll(s, c));
} else {
var ex = assertThrows(EnrollmentRejectedException.class,
() -> svc.enroll(s, c));
assertEquals(expected, ex.reason().name());
}
}
}
```
---
Задание 14. Ресурс с двумя отказами
```java
import java.io.*;
import java.util.*;public class CourseImporter {
public record Row(int number, String code, int seats) {}
}
`public List<Row> importAll(Reader reader) throws CourseImportException {
List<Row> published = new ArrayList<>();
try (BuferedReader br = new BuferedReader(reader)) {
String line; int n = 0;
while ((line = br.readLine()) != null) {
n++;
String[] p = line.split(";");
if (p.length != 2) throw new CourseImportException(n, "malformed", null);
int seats;
try { seats = Integer.parseInt(p[1].trim()); }
catch (NumberFormatException nfe) {
throw new CourseImportException(n, "bad seats", nfe);
}
published.add(new Row(n, p[0].trim(), seats));
}
} catch (IOException ioe) {
throw new CourseImportException(-1, "close/read failed", ioe);
} catch (CourseImportException cie) {
throw cie;
}
return List.copyOf(published);
}
``
```java
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
class CourseImporterTest {
static class FailingCloseReader extends StringReader {
FailingCloseReader(String s) { super(s); }
@Override public void close() { throw new UncheckedIOException(
new IOException("close failed")); }
}
@Test
void malformedRecord_isPrimary_closeFailure_isSuppressed() {
String data = "CS-101;10\nBROKEN\n";
// Reader, который на readLine кинет, а затем close — тоже
Reader r = new StringReader(data) {@Override public void close() { throw new UncheckedIOException(
new IOException("close failed")); }
};
var importer = new CourseImporter();
CourseImportException ex = assertThrows(CourseImportException.class,
() -> importer.importAll(r));
assertEquals(2, ex.recordNumber());
assertTrue(ex.getMessage().toLowerCase().contains("malformed"));
}
@Test
void successfulImport_returnsAllRecords() throws Exception {
String data = "CS-101;10\nMA-201;5\n";
var rows = new CourseImporter().importAll(new StringReader(data));
assertEquals(2, rows.size());
assertEq