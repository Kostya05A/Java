package task4;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Task4Main {
    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task4-");
        Path file = workDir.resolve("lines.txt");
        Files.writeString(file, "alpha\nbeta\n\ngamma\n", StandardCharsets.UTF_8);

        long nonBlank = countNonBlankInsideTry(file);
        System.out.println("Корректный вариант: Files.lines внутри try-with-resources, непустых строк = " + nonBlank);

        System.out.println();
        System.out.println("Вариант с передачей владения наружу: метод возвращает открытый Stream.");
        System.out.println("Он корректен только если вызывающий код явно принимает владение и закрывает поток:");
        try (Stream<String> lines = openAndReturnStream(file)) {
            System.out.println("  строк во внешнем try-with-resources = " + lines.count());
        }

        System.out.println();
        System.out.println("Ошибочный вариант: return stream из try-with-resources.");
        System.out.println("Поток закрывается до возврата, поэтому использовать его уже нельзя:");
        boolean failed = false;
        try {
            Stream<String> closed = returnStreamFromTry(file);
            closed.count();
        } catch (IllegalStateException e) {
            failed = true;
            System.out.println("  ожидаемая ошибка: " + e.getMessage());
        }
        if (!failed) {
            throw new IllegalStateException("Ожидалась ошибка использования закрытого потока");
        }

        System.out.println();
        System.out.println("Контракт владения: ресурсом владеет тот, кто открыл Files.lines.");
        System.out.println("Если поток возвращают наружу, владение переходит вызывающему коду и должно быть явно задокументировано.");
        System.out.println("Возврат из try-with-resources всегда ошибочен: TWR закрывает поток до выхода из метода.");
    }

    private static long countNonBlankInsideTry(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.filter(line -> !line.isBlank()).count();
        }
    }

    private static Stream<String> openAndReturnStream(Path path) throws IOException {
        return Files.lines(path, StandardCharsets.UTF_8);
    }

    private static Stream<String> returnStreamFromTry(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines;
        }
    }
}
