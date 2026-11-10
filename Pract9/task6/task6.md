# Task 6 — Запись на курс с ограничением мест

Файлы: `EnrollmentService.java`, `SynchronizedEnrollment.java`,
`AtomicEnrollment.java`, `ConcurrentMapEnrollment.java`, `Task6Demo.java`.
Тест: `test/CourseEnrollmentTest.java`.

Контракт `enroll`:

- успешных записей не больше `capacity`;
- один `studentId` не занимает место дважды;
- отказ (`false`) не изменяет итоговое состояние.

## Вариант 1 — coarse-grained lock

`SynchronizedEnrollment` держит один монитор на весь объект: проверка дубликата,
проверка свободного места и добавление выполняются в одной критической секции.
Корректность очевидна, но все записи сериализуются, поэтому при высокой
конкуренции throughput ограничен.

## Вариант 2 — атомарные операции

`AtomicEnrollment` использует `ConcurrentHashMap.newKeySet()` для уникальности
студентов и `AtomicInteger` с CAS-циклом для мест. Место резервируется только
если `used < capacity`; при отказе студент удаляется из set. Глобальной
блокировки нет, но операция состоит из нескольких шагов и требует rollback.

## Вариант 3 — concurrent collections

`ConcurrentMapEnrollment` использует `Semaphore(capacity)` как атомарный
счётчик разрешений и `ConcurrentHashMap.putIfAbsent` для уникальности. Если
студент уже записан, разрешение возвращается. Ёмкость ограничена семафором,
уникальность — concurrent map.

## Сравнение

| Вариант | Плюсы | Минусы |
| --- | --- | --- |
| `synchronized` | проще всего доказать корректность | один монитор, низкий параллелизм |
| atomic | нет глобальной блокировки, хороший throughput | несколько шагов и rollback |
| concurrent map + semaphore | атомарное ограничение мест + concurrent map | две структуры, нужен возврат разрешения |

Тест запускает много потоков одновременно, проверяет точное число успехов при
уникальных студентах, отсутствие второго места для одного студента и то, что
отказ не меняет состояние.

Запуск:

```bash
mvn -q -Dtest=CourseEnrollmentTest test
mvn -q compile
java -cp target/classes task6.Task6Demo
```
