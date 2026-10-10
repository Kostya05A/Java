import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.Reader;
import static org.junit.jupiter.api.Assertions.*;

class CourseImporterTest {
    
    static class FailingReader extends Reader {
        private final String data;
        private int pos = 0;
        
        FailingReader(String data) {
            this.data = data;
        }
        
        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (pos >= data.length()) return -1;
            int charsRead = Math.min(len, data.length() - pos);
            data.getChars(pos, pos + charsRead, cbuf, off);
            pos += charsRead;
            return charsRead;
        }
        
        @Override
        public void close() throws IOException {
            throw new IOException("Close failed");
        }
    }
    
    @Test
    void handlesMalformedAndCloseErrors() {
        Reader failingReader = new FailingReader("abc!def");

        CourseImporter importer = new CourseImporter();
        IOException ex = assertThrows(IOException.class, () -> importer.importCourses(failingReader));
        
        assertEquals("Malformed record", ex.getMessage());
        assertEquals(1, ex.getSuppressed().length);
        assertEquals("Close failed", ex.getSuppressed()[0].getMessage());
    }
}