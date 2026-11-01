package task6;

import src.Course;
import src.CourseFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public class Task6Main {
    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task6-");
        Path target = workDir.resolve("courses.txt");
        Files.writeString(target, "старый каталог\n", StandardCharsets.UTF_8);

        List<Course> first = List.of(
                CourseFactory.create(1, "Атомарная замена", 20, 5),
                CourseFactory.create(2, "Временный файл", 15, 0)
        );
        saveCatalogAtomically(target, first);
        System.out.println("После atomic move:");
        System.out.print(Files.readString(target, StandardCharsets.UTF_8));

        List<Course> second = List.of(
                CourseFactory.create(3, "Fallback без atomic move", 30, 10)
        );

        MoveOperation withoutAtomicMove = (source, destination, options) -> {
            for (CopyOption option : options) {
                if (option == StandardCopyOption.ATOMIC_MOVE) {
                    throw new AtomicMoveNotSupportedException(
                            source.toString(),
                            destination.toString(),
                            "демонстрация отсутствия atomic move");
                }
            }
            Files.move(source, destination, options);
        };

        saveCatalogAtomically(target, second, withoutAtomicMove);
        System.out.println("\nПосле fallback без atomic move:");
        System.out.print(Files.readString(target, StandardCharsets.UTF_8));

        try (Stream<Path> files = Files.list(workDir)) {
            long temporaryLeftovers = files
                    .filter(path -> path.getFileName().toString().endsWith(".tmp"))
                    .count();
            System.out.println("\nОставшихся временных файлов: " + temporaryLeftovers);
        }
    }

    private static void saveCatalogAtomically(Path target, List<Course> courses) throws IOException {
        saveCatalogAtomically(target, courses, Files::move);
    }

    private static void saveCatalogAtomically(Path target, List<Course> courses, MoveOperation moveOperation)
            throws IOException {
        Path absoluteTarget = target.toAbsolutePath();
        Path parent = absoluteTarget.getParent();
        if (parent == null) {
            throw new IOException("Не удалось определить каталог целевого файла: " + target);
        }
        Files.createDirectories(parent);

        Path temporary = Files.createTempFile(parent, "catalog-", ".tmp");
        boolean moved = false;
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporary,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                for (Course course : courses) {
                    writer.write(course.id() + "|" + course.title() + "|"
                            + course.durationHours() + "|" + course.completedHours());
                    writer.newLine();
                }
                writer.flush();
            }

            replaceWithFallback(temporary, absoluteTarget, moveOperation);
            moved = true;
        } finally {
            if (!moved) {
                Files.deleteIfExists(temporary);
            }
        }
    }

    private static void replaceWithFallback(Path temporary, Path target, MoveOperation moveOperation)
            throws IOException {
        try {
            moveOperation.move(
                    temporary,
                    target,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Целевой файл заменён атомарным move.");
        } catch (AtomicMoveNotSupportedException e) {
            System.out.println("Atomic move не поддерживается файловой системой: " + e.getReason());
            System.out.println("Выполняется обычный move с REPLACE_EXISTING.");
            moveOperation.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @FunctionalInterface
    private interface MoveOperation {
        void move(Path source, Path target, CopyOption... options) throws IOException;
    }
}
