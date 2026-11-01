package task5;

import src.Course;
import src.CourseFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task5Main {
    private static final int MAGIC = 0x43555253;
    private static final short VERSION = 1;
    private static final int MAX_RECORDS = 1_000_000;

    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task5-");
        Path catalog = workDir.resolve("courses.bin");

        List<Course> courses = List.of(
                CourseFactory.create(1, "Бинарные форматы", 20, 5),
                CourseFactory.create(2, "DataInputStream", 12, 0),
                CourseFactory.create(3, "Контроль формата", 8, 8)
        );

        writeCatalog(catalog, courses);
        List<Course> loaded = readCatalog(catalog);
        System.out.println("Корректный файл прочитан:");
        loaded.forEach(course -> System.out.printf(
                "  id=%d, title=%s, duration=%d, completed=%d%n",
                course.id(), course.title(), course.durationHours(), course.completedHours()));

        byte[] validBytes = Files.readAllBytes(catalog);

        byte[] unknownVersionBytes = validBytes.clone();
        ByteBuffer.wrap(unknownVersionBytes).putShort(4, (short) 99);
        Path unknownVersionFile = workDir.resolve("unknown-version.bin");
        Files.write(unknownVersionFile, unknownVersionBytes);
        expectFormatError("неизвестная версия", unknownVersionFile);

        Path truncatedFile = workDir.resolve("truncated.bin");
        Files.write(truncatedFile, Arrays.copyOf(validBytes, validBytes.length - 9));
        expectFormatError("обрезанный файл", truncatedFile);
    }

    private static void writeCatalog(Path path, List<Course> courses) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                Files.newOutputStream(
                        path,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)))) {
            out.writeInt(MAGIC);
            out.writeShort(VERSION);
            out.writeInt(courses.size());
            for (Course course : courses) {
                out.writeLong(course.id());
                out.writeUTF(course.title());
                out.writeInt(course.durationHours());
                out.writeInt(course.completedHours());
            }
        }
    }

    private static List<Course> readCatalog(Path path) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            int magic = readInt(in, "magic header");
            if (magic != MAGIC) {
                throw new FormatException("Неверный magic header: 0x" + Integer.toHexString(magic));
            }

            short version = readShort(in, "версию");
            if (version != VERSION) {
                throw new FormatException("Неизвестная версия формата: " + version
                        + ", поддерживается только " + VERSION);
            }

            int count = readInt(in, "количество записей");
            if (count < 0 || count > MAX_RECORDS) {
                throw new FormatException("Недопустимое количество записей: " + count);
            }

            List<Course> result = new ArrayList<>(Math.min(count, 10_000));
            for (int index = 0; index < count; index++) {
                try {
                    long id = in.readLong();
                    String title = in.readUTF();
                    int duration = in.readInt();
                    int completed = in.readInt();
                    validateRecord(id, title, duration, completed, index + 1);
                    result.add(CourseFactory.create(id, title, duration, completed));
                } catch (EOFException e) {
                    throw new FormatException("Файл обрезан в записи " + (index + 1), e);
                } catch (UTFDataFormatException e) {
                    throw new FormatException("Повреждённое название в записи " + (index + 1), e);
                }
            }

            if (in.read() != -1) {
                throw new FormatException("После заявленного количества записей есть лишние данные");
            }
            return List.copyOf(result);
        } catch (EOFException e) {
            throw new FormatException("Файл обрезан: не удалось прочитать заголовок", e);
        }
    }

    private static int readInt(DataInputStream in, String field) throws IOException {
        try {
            return in.readInt();
        } catch (EOFException e) {
            throw new FormatException("Файл обрезан: отсутствует " + field, e);
        }
    }

    private static short readShort(DataInputStream in, String field) throws IOException {
        try {
            return in.readShort();
        } catch (EOFException e) {
            throw new FormatException("Файл обрезан: отсутствует " + field, e);
        }
    }

    private static void validateRecord(long id, String title, int duration, int completed, int recordNumber)
            throws FormatException {
        if (id <= 0 || title.isBlank() || duration < 1 || duration > 100
                || completed < 0 || completed >= duration) {
            throw new FormatException("Некорректная запись " + recordNumber);
        }
    }

    private static void expectFormatError(String label, Path path) throws IOException {
        boolean failed = false;
        try {
            readCatalog(path);
        } catch (FormatException e) {
            failed = true;
            System.out.println("\n" + label + " -> ожидаемый отказ:");
            System.out.println("  " + e.getMessage());
        }
        if (!failed) {
            throw new IllegalStateException("Ожидалась ошибка формата для сценария: " + label);
        }
    }

    private static final class FormatException extends IOException {
        private FormatException(String message) {
            super(message);
        }

        private FormatException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
