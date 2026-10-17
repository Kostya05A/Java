import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Путь передается снаружи - тест сам решает, откуда читать данные.
// Это и есть фикс зависимости от текущей директории.
public class CourseCatalog {
  public static List<CourseRecord> loadCsv(Path path) throws IOException {
    List<CourseRecord> records = new ArrayList<>();
    for (String line : Files.readAllLines(path)) {
      if (line.isBlank()) {
        continue;
      }
      String[] parts = line.split(";");
      records.add(new CourseRecord(
          Long.parseLong(parts[0].trim()),
          parts[1].trim(),
          Integer.parseInt(parts[2].trim())));
    }
    return records;
  }
}