package level5.Demo14;

import java.io.IOException;
import java.io.Reader;

public class CourseImporter {
    public int importCourses(Reader reader) throws IOException {
        int count = 0;
        try (reader) {
            int ch;
            while ((ch = reader.read()) != -1) {
                if (ch == '!') {
                    throw new IOException("Malformed record");
                }
                count++;
            }
        } catch (IOException e) {
            try {
                reader.close();
            } catch (IOException closeEx) {
                e.addSuppressed(closeEx);
            }
            throw e;
        }
        return count;
    }
}
