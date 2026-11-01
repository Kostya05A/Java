import src.Course;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

// Импортёр читает записи построчно. Если запись сломана — бросает
// CourseImportException с номером записи и cause. Если закрылся ресурс —
// IOException в close превращается в suppressed (try-with-resources).
// Никакой частичной публикации: данные накапливаются во временном списке,
// публикуем ТОЛЬКО в конце, когда все записи успешно прочитаны.
public class CourseImporter {
  private final CourseStore store;

  public CourseImporter(CourseStore store) {
    this.store = store;
  }

  public int importFrom(Reader reader) throws IOException, CourseImportException {
    if (reader == null) throw new IllegalArgumentException("reader must not be null");
    List<Course> buffer = new ArrayList<>();
    BufferedReader br = new BufferedReader(reader);
    String line;
    int recordNumber = 0;
    try (br) {
      while ((line = br.readLine()) != null) {
        recordNumber++;
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
        try {
          Course c = parseRecord(trimmed, recordNumber);
          buffer.add(c);
        } catch (Exception ex) {
          throw new CourseImportException(recordNumber,
              "Invalid record at line " + recordNumber, ex);
        }
      }
      // публикация только целиком: buffer содержит все успешно прочитанные записи
      store.publish(buffer);
      return buffer.size();
    } catch (CourseImportException e) {
      throw e;
    } catch (IOException ex) {
      throw ex;
    } catch (Throwable ex) {
      throw new CourseImportException(recordNumber,
          "Invalid record at line " + recordNumber, ex);
    }
  }

  private Course parseRecord(String line, int recordNumber) {
    // формат: id;title;hours
    String[] parts = line.split(";", 3);
    if (parts.length != 3) {
      throw new IllegalArgumentException("bad format");
    }
    long id = Long.parseLong(parts[0].trim());
    String title = parts[1].trim();
    int hours = Integer.parseInt(parts[2].trim());
    return new Course(id, title, hours);
  }
}