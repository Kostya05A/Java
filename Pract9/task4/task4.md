# Task 4 — Producer–consumer на ограниченной `BlockingQueue`

Файлы: `EnrollmentRequest.java`, `ProducerConsumerDemo.java`.

`ProducerConsumerDemo.run` создаёт `ArrayBlockingQueue` заданной ёмкости.
Несколько producer-задач делят входной список заявок и кладут их через
блокирующий `put`. Несколько consumer-задач забирают заявки через `take` и
учитывают уникально обработанные `id`.

## Backpressure

Ёмкость очереди — это предел числа заявок «в полёте». Когда очередь заполнена,
`put` блокирует producer, пока consumer не освободит место. Producer не
крутится в busy waiting и не накапливает неограниченный буфер: скорость
производства автоматически подстраивается под скорость обработки.

## Корректное завершение

После того как все producers закончили, `CountDownLatch` отпускает главный
поток. В очередь добавляется по одному poison pill на каждого consumer.
Consumer, получивший poison pill, выходит из цикла штатно. Никаких опросов
флага и `Thread.sleep` как средства синхронизации нет.

## `ExecutorService`

Producers и consumers работают в отдельных пулах с осмысленными именами
потоков (`coursehub-producer-*`, `coursehub-consumer-*`). Для обоих пулов
вызывается `shutdown`, затем ограниченное `awaitTermination`; если потоки не
завершились за отведённое время, выполняется аварийный `shutdownNow`. Блок
`finally` повторно вызывает `shutdownNow`, поэтому пулы освобождаются даже при
исключении.

## Размер пула

- CPU-bound: размер пула примерно равен числу доступных ядер.
- Blocking-сценарий (I/O, ожидание): потоков больше, ориентир
  `threads ≈ cores * (1 + wait/compute)`.

В демо consumers имитируют блокирующую обработку, поэтому consumer-пул может
быть больше числа ядер.

Запуск:

```bash
mvn -q compile
java -cp target/classes task4.ProducerConsumerDemo
```
