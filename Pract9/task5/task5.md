# Task 5 — Детерминированный конкурентный тест

Файлы: `ProtocolCounter.java`, `DeterministicRaceDemo.java`, тест
`test/DeterministicRaceTest.java`.

`ProtocolCounter.unsafeIncrementWithProtocol` разложен на три шага:

1. чтение `value`;
2. точка протокола — `Phaser.arriveAndAwaitAdvance()`;
3. запись `read + 1`.

Обе нити сначала читают одно и то же значение и встречаются на `Phaser`.
Только после того, как обе дошли до барьера, каждая выполняет запись. Поэтому
потерянный инкремент воспроизводится детерминированно: итог всегда `1`, а не
`2`. `Thread.sleep` для синхронизации не используется.

Тест запускает нити через `ExecutorService`, управляет стартом через
`CountDownLatch`, имеет `@Timeout` и в `finally` вызывает `shutdownNow` с
ограниченным `awaitTermination`. Второй тест проверяет, что
синхронизированный инкремент при таком же управляемом старте не теряет
обновления.

Запуск:

```bash
mvn -q -Dtest=DeterministicRaceTest test
java -cp target/classes task5.DeterministicRaceDemo
```
