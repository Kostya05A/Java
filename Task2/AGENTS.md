# AGENTS.md — CourseHub, задания 1–15 по обработке ошибок

## Что это

Учебный проект: 15 заданий на Java по обработке ошибок — от чтения
stack trace до публичного слоя со стабильными кодами ошибок. Каждое
задание разобрано в `src/levelN/заданиеN.md`: теория рядом с кодом,
который её иллюстрирует.

## Сборка и запуск

Maven в системе нет, используется wrapper.

```bash
./mvnw -q compile                 # компиляция
./mvnw test                       # все тесты (156, 2 отключены как демонстрация)
./mvnw test -Dtest=CourseCodeTest # один класс
./mvnw test -Dtest='FragileTestsFixedTest#isSelfContained'   # один метод

java -cp target/classes level1.Demo1     # демонстрации из src/levelN
java -cp target/classes level5.CourseImporter
java -cp target/classes level5.CourseHub
```

`Demo2` намеренно вызывает `OutOfMemoryError` — процесс завершится
с ошибкой, это ожидаемо.

## Структура

```
src/level1/    задания 1-3:  stack trace, checked/unchecked, своё исключение
src/level2/    задания 4-7:  finally+try-with-resources, AutoCloseable,
                            перевод исключений, ошибка как значение
src/level3/    задания 8-10: тесты границ, параметризованные тесты
src/level4/    задания 11-12: assertThrows, состояние после отказа, хрупкие тесты
src/level5/    задания 13-15: таблица решений, импорт с откатом, публичный слой
src/domain/    Course, CourseCode, CourseStatus, EnrollmentResult, EnrollmentService, ErrorCode
test/levelN/   тесты к уровню N
src/test/resources/testdata/courses.csv   фикстура для тестов
```

## Соглашения

* **Пакет совпадает с папкой.** `src/domain` → `package domain`,
  `src/levelN` → `package levelN`, `test/levelN` → `package levelN`.
  Eclipse JDT в VSCode проверяет это строго и при несовпадении сыплет
  «The declared package does not match the expected package» по всему
  проекту; `javac` такой код собирает молча. Правя раскладку папок —
  правь `package` во всех файлах.
* Имена пакетов совпадают с именами папок лабораторных (`level1`…`level5`),
  доменный код лежит в `domain`. Именованные пакеты нужны и для импортов:
  из default package импортировать нельзя.
* Вложенные типы доступны и через импорт: `EnrollmentService.CourseCatalog`.
* **Java 21**, UTF-8, 4 пробела.
* **Комментариев в Java-коде нет.** Вся теория, обоснования и разборы живут
  в `src/levelN/заданиеN.md` рядом с кодом, который их иллюстрирует.
  Новый комментарий в `.java` не писать — дописывать нужный раздел в `.md`.
* Текст в `.md` и сообщения в коде — по-русски, идентификаторы — по-английски.
* `Demo`-классы содержат `main` и самодостаточны для запуска.
* Тесты не зависят от порядка: surefire работает с `runOrder=random`.
  Общего `static`-состояния в тестах нет, `@BeforeEach` создаёт своё.

## Границы проекта

* Не обращаться к соседним каталогам `../Task0`, `../Task1`, `../java` —
  доступ к ним запрещён.
* `target/` — сгенерированное, не редактировать.
* Git в проекте не используется, коммиты не создавать без запроса.

## Карта решений (чтобы не переобъяснять)

| Вопрос | Решение | Где |
|---|---|---|
| Отсутствие в поиске | `Optional` | `CourseService.findCourse` |
| Отказ с несколькими причинами | типизированный `ParseResult` | `CourseService.parseCourse` |
| Отказ зачисления | значение `EnrollmentResult` | `EnrollmentService.enroll` |
| Нужен успех или ничего | `enrollOrFail` → `EnrollmentRejectedException` | `EnrollmentService` |
| Границы значения | `IllegalArgumentException` до изменения состояния | `Course.completeHours` |
| Низкоуровневый сбой | checked с `cause` и контекстом | `CourseImportException` |
| Импорт | снимок состояния + `restore` | `CourseImporter.importFrom` |
| Публичная граница | `OperationResult` + `ErrorCode`, исключений нет | `CourseHub` |

`ErrorCode` — часть контракта: текст в нём менять можно, сами значения —
нельзя. Новые причины отказа добавляются туда, а не в тексты.
