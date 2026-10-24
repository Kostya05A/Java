package task9;

import src.Course;
import src.CourseFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class Task9Main {
    private static final int MAGIC = 0x43555239;
    private static final int CURRENT_VERSION = 2;
    private static final int MAX_PAYLOAD = 64 * 1024 * 1024;
    private static final int MAX_RECORDS = 1_000_000;

    public static void main(String[] args) throws Exception {
        Path workDir = Files.createTempDirectory("task9-");

        List<Course> courses = List.of(
                CourseFactory.create(1, "Версионируемый формат", 30, 7),
                CourseFactory.create(2, "CRC и миграция", 20, 0)
        );

        Path version2File = workDir.resolve("version2.bin");
        writeVersion2(version2File, courses);
        printResult("Чтение актуальной версии 2", readCatalog(version2File));

        Path version1File = workDir.resolve("version1.bin");
        writeVersion1(version1File, List.of(
                CourseFactory.create(10, "Старый формат", 25),
                CourseFactory.create(11, "Курс без completed", 10)
        ));
        printResult("Чтение версии 1 с миграцией в версию 2", readCatalog(version1File));

        byte[] version2Bytes = Files.readAllBytes(version2File);

        byte[] futureVersionBytes = version2Bytes.clone();
        ByteBuffer.wrap(futureVersionBytes).putInt(4, CURRENT_VERSION + 1);
        Path futureVersionFile = workDir.resolve("future-version.bin");
        Files.write(futureVersionFile, futureVersionBytes);
        printResult("Будущая версия не должна читаться как старая", readCatalog(futureVersionFile));

        byte[] corruptedBytes = version2Bytes.clone();
        corruptedBytes[corruptedBytes.length - 1] ^= (byte) 0xFF;
        Path corruptedFile = workDir.resolve("corrupted.bin");
        Files.write(corruptedFile, corruptedBytes);
        printResult("Повреждение payload обнаруживается до публикации объектов",
                readCatalog(corruptedFile));
    }

    private static void printResult(String label, ReadResult result) {
        System.out.println("\n" + label + ":");
        if (result instanceof ReadSuccess success) {
            System.out.println("  версия источника: " + success.sourceVersion());
            success.courses().forEach(course -> System.out.printf(
                    "  id=%d, title=%s, duration=%d, completed=%d%n",
                    course.id(), course.title(), course.durationHours(), course.completedHours()));
        } else if (result instanceof ReadFailure failure) {
            System.out.println("  отказ: " + failure.message());
            System.out.println("  объекты не опубликованы");
        }
    }

    private static void writeVersion2(Path path, List<Course> courses) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream payload = new DataOutputStream(buffer)) {
            payload.writeInt(courses.size());
            for (Course course : courses) {
                payload.writeLong(course.id());
                payload.writeUTF(course.title());
                payload.writeInt(course.durationHours());
                payload.writeInt(course.completedHours());
            }
        }
        writeContainer(path, CURRENT_VERSION, buffer.toByteArray());
    }

    private static void writeVersion1(Path path, List<Course> courses) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (DataOutputStream payload = new DataOutputStream(buffer)) {
            payload.writeInt(courses.size());
            for (Course course : courses) {
                payload.writeLong(course.id());
                payload.writeUTF(course.title());
                payload.writeInt(course.durationHours());
            }
        }
        writeContainer(path, 1, buffer.toByteArray());
    }

    private static void writeContainer(Path path, int version, byte[] payload) throws IOException {
        CRC32 crc = new CRC32();
        crc.update(payload);

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                Files.newOutputStream(
                        path,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE)))) {
            out.writeInt(MAGIC);
            out.writeInt(version);
            out.writeInt(payload.length);
            out.writeLong(crc.getValue());
            out.write(payload);
        }
    }

    private static ReadResult readCatalog(Path path) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
            int magic = in.readInt();
            if (magic != MAGIC) {
                return new ReadFailure("неверный magic header: 0x" + Integer.toHexString(magic));
            }

            int version = in.readInt();
            if (version > CURRENT_VERSION) {
                return new ReadFailure("неизвестная будущая версия " + version
                        + "; она не может читаться как версия " + CURRENT_VERSION);
            }
            if (version != 1 && version != 2) {
                return new ReadFailure("неподдерживаемая версия " + version);
            }

            int payloadLength = in.readInt();
            if (payloadLength < 0 || payloadLength > MAX_PAYLOAD) {
                return new ReadFailure("недопустимая длина payload: " + payloadLength);
            }
            long expectedChecksum = in.readLong();
            byte[] payload = in.readNBytes(payloadLength);
            if (payload.length != payloadLength) {
                return new ReadFailure("файл обрезан: payload короче заявленной длины");
            }
            if (in.read() != -1) {
                return new ReadFailure("после payload обнаружены лишние данные");
            }

            CRC32 crc = new CRC32();
            crc.update(payload);
            if (crc.getValue() != expectedChecksum) {
                return new ReadFailure("checksum не совпал: ожидался "
                        + expectedChecksum + ", получен " + crc.getValue());
            }

            return parsePayload(version, payload);
        } catch (EOFException e) {
            return new ReadFailure("файл обрезан: не удалось прочитать заголовок");
        } catch (IOException e) {
            return new ReadFailure("ошибка чтения: " + e.getMessage());
        }
    }

    private static ReadResult parsePayload(int version, byte[] payload) {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(payload))) {
            int count = in.readInt();
            if (count < 0 || count > MAX_RECORDS) {
                return new ReadFailure("недопустимое количество записей: " + count);
            }

            List<Course> courses = new ArrayList<>(Math.min(count, 10_000));
            for (int index = 0; index < count; index++) {
                long id = in.readLong();
                String title = in.readUTF();
                int duration = in.readInt();
                int completed = version >= 2 ? in.readInt() : 0;

                validate(id, title, duration, completed, index + 1);
                Course course = CourseFactory.create(id, title, duration, completed);
                if (version == 1) {
                    course = migrateVersion1ToVersion2(course);
                }
                courses.add(course);
            }

            if (in.read() != -1) {
                return new ReadFailure("в payload есть лишние данные");
            }
            return new ReadSuccess(courses, version);
        } catch (EOFException e) {
            return new ReadFailure("payload обрезан");
        } catch (IllegalArgumentException e) {
            return new ReadFailure("некорректные данные в payload: " + e.getMessage());
        } catch (IOException e) {
            return new ReadFailure("повреждённый payload: " + e.getMessage());
        }
    }

    private static Course migrateVersion1ToVersion2(Course version1) {
        return CourseFactory.create(
                version1.id(),
                version1.title(),
                version1.durationHours(),
                version1.completedHours());
    }

    private static void validate(long id, String title, int duration, int completed, int recordNumber) {
        if (id <= 0 || title.isBlank() || duration < 1 || duration > 100
                || completed < 0 || completed >= duration) {
            throw new IllegalArgumentException("некорректная запись " + recordNumber);
        }
    }

    private sealed interface ReadResult permits ReadSuccess, ReadFailure {
    }

    private record ReadSuccess(List<Course> courses, int sourceVersion) implements ReadResult {
        private ReadSuccess {
            courses = List.copyOf(courses);
        }
    }

    private record ReadFailure(String message) implements ReadResult {
    }
}
