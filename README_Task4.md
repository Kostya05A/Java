Рекомендуемая структура исходников
src/main/java/coursehub/practice6/
  strings/
    StringIdentityDemo.java
    ReportBuilder.java
    CourseCode.java
  numbers/
    IntegerPolicy.java
    DecimalDemo.java
    DiscountAllocator.java
    RandomSampleService.java
    SampleStats.java
  time/
    StudyCalendar.java
    DateParser.java
    InvalidDateFieldException.java
    WebinarSchedule.java
    LocalDateTimeResolver.java
    InvalidLocalTimeException.java
  domain/
    CourseTitle.java
    Money.java
    WebinarSeriesPlan.java
    CourseOffering.java
    EarlyBirdPolicy.java
    PricingService.java
    Purchase.java
    RefundService.java

src/test/java/coursehub/practice6/
  ...тесты с теми же пакетами...

Задание 1. `String`: identity и value
Предскажите `==` и `equals` для литералов, `new String`, concatenation compile-time/runtime и `intern`. Сформулируйте правило сравнения текста, не опирающееся на pool.

Задание 2. Сборка строки
Сформируйте отчёт из 10 000 строк через `+` в цикле и `StringBuilder`. Сравните результат и время как учебный эксперимент, не выдавая его за JMH. Объясните неизменяемость `String`.

Задание 3. Регулярное выражение с группами
Проверьте код курса вида `JAVA-101`, извлеките prefix и number, запретите частичное совпадение. Подготовьте таблицу валидных и невалидных значений, включая Unicode и пробелы.

Задание 4. Autoboxing и `null` 
Воспроизведите NPE при unboxing `Integer`, сравните `Integer` через `==` на разных значениях и исправьте код через явный контракт. Не полагайтесь на cache wrapper-объектов.

Задание 5. `BigDecimal` без сюрприза double
Сравните `new BigDecimal(0.1)`, `BigDecimal.valueOf(0.1)` и строковый конструктор. Рассчитайте стоимость трёх курсов и объясните разницу представлений.

Задание 6. Масштаб и округление
Распределите скидку между позициями так, чтобы итоговая сумма точно совпала с ожидаемой. Режим округления передаётся явно; остаток копеек распределяется детерминированно.

Задание 7. `Math` и воспроизводимая случайность
Сгенерируйте учебную выборку через `RandomGenerator` с seed, вычислите границы и округление. Разделите production randomness и детерминированный тестовый источник.

Задание 8. `LocalDate` и календарные правила
Рассчитайте даты начала модулей, последний учебный день и попадание в leap year. Исходные объекты не меняются; результат операций присваивается.

Задание 9. `Period` против `Duration`
Сравните «один календарный день» и «24 часа». Подберите пример, где эти понятия расходятся после добавления зоны. Объясните, какой тип подходит сроку подписки, а какой timeout.

Задание 10. Строгий `DateTimeFormatter`
Парсите даты формата `dd.MM.uuuu`, отклоняя невозможные даты и лишний текст. Сообщение ошибки содержит поле и исходное значение, но не скрывает cause без причины.

Задание 11. `Instant` и пользовательская зона
Храните момент вебинара как `Instant`, отображайте в трёх `ZoneId`. Покажите, почему серверная default zone не должна неявно менять бизнес-результат.

Задание 12. DST-кейс
Найдите в выбранной зоне пропущенное или повторённое локальное время. Исследуйте создание `ZonedDateTime`, добавление часа и дня. Зафиксируйте политику для несуществующего времени занятия.

Задание 13. Нормализованный поиск
Создайте pipeline нормализации названий: strip, схлопывание пробелов, case folding по фиксированной locale. Отделите display value от search key и покажите конфликт двух разных исходных строк.

Задание 14. `Money` и цена курса
Спроектируйте неизменяемый `Money` с amount и currency, запретом смешения валют, явным округлением и содержательным равенством. Реализуйте add, subtract, percentage и allocation без double.

Задание 15. Расписание и цена вокруг границ времени
CourseHub продаёт вебинар в зоне организатора пользователям разных зон, применяет early-bird до конкретного момента и возвращает деньги пропорционально оставшимся занятиям. Реализуйте модель на `Instant`, `ZonedDateTime`, `LocalDate`, `Duration/Period` и `BigDecimal`. Покройте DST, leap day, границу скидки, разные валюты и округление. Защитите выбор каждого типа.
