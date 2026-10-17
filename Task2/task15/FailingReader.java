import java.io.IOException;
import java.io.Reader;

public class FailingReader extends Reader {
  private final String[] lines;
  private final int malformedAt;
  private int index;
  private final boolean failOnClose;
  private boolean closed;

  public FailingReader(String[] lines, int malformedAt, boolean failOnClose) {
    this.lines = lines == null ? new String[0] : lines;
    this.malformedAt = malformedAt;
    this.failOnClose = failOnClose;
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    if (index < lines.length) {
      if (malformedAt >= 1 && index == malformedAt - 1) {
        // Содержимое "битой" записи отдаём целиком, чтобы
        // BufferedReader прочитал её как строку №3.
        // Ошибку записи будем симулировать позже при парсинге? Но мы контролируем строку.
        // Просто отдадим строку и выставим флаг: при следующем? Или лучше отдать строку,
        // но в импортере мы уже обработали предыдущие — строка будет прочитана.
        String s = lines[index++] + "\n";
        char[] data = s.toCharArray();
        System.arraycopy(data, 0, cbuf, off, Math.min(len, data.length));
        return Math.min(len, data.length);
      }
      String s = lines[index++] + "\n";
      char[] data = s.toCharArray();
      System.arraycopy(data, 0, cbuf, off, Math.min(len, data.length));
      return Math.min(len, data.length);
    }
    return -1;
  }

  @Override
  public void close() throws IOException {
    closed = true;
    if (failOnClose) {
      throw new IOException("close failed");
    }
  }

  public boolean closed() {
    return closed;
  }
}