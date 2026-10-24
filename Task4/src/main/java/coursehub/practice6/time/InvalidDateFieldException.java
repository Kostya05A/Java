package coursehub.practice6.time;

// сообщение содержит поле и исходное значение; cause сохраняем, а не прячем
public class InvalidDateFieldException extends RuntimeException {
  private final String field;
  private final String value;

  public InvalidDateFieldException(String field, String value, Throwable cause) {
    super("invalid " + field + ": '" + value + "'", cause);
    this.field = field;
    this.value = value;
  }

  public String field() {
    return field;
  }

  public String value() {
    return value;
  }
}
