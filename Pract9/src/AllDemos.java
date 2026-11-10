package src;

import task1.LostIncrementsDemo;
import task2.Task2Demo;
import task3.SessionRegistryDemo;
import task4.ProducerConsumerDemo;
import task5.DeterministicRaceDemo;
import task6.Task6Demo;

public final class AllDemos {
    private AllDemos() {
    }

    public static void main(String[] args) throws Exception {
        run("Task 1 — Потерянные инкременты", () -> LostIncrementsDemo.main(args));
        run("Task 2 — Флаг остановки и visibility", () -> Task2Demo.main(args));
        run("Task 3 — Потокобезопасный реестр", () -> SessionRegistryDemo.main(args));
        run("Task 4 — Producer–consumer", () -> ProducerConsumerDemo.main(args));
        run("Task 5 — Детерминированный конкурсный тест", () -> DeterministicRaceDemo.main(args));
        run("Task 6 — Запись на курс с ограничением мест", () -> Task6Demo.main(args));
    }

    private static void run(String title, Demo demo) throws Exception {
        System.out.println();
        System.out.println("");
        System.out.println(title);
        System.out.println("");
        demo.run();
    }

    @FunctionalInterface
    private interface Demo {
        void run() throws Exception;
    }
}
