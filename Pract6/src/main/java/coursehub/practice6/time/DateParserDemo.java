package coursehub.practice6.time;

// отчётливо видно, какие строки принимаются, а какие нет
public class DateParserDemo {
  public static void main(String[] args) {
    String[] values = {
        "01.02.2024",
        "29.02.2024",   // високосный год — валидно
        "29.02.2023",   // не високосный — невалидно
        "31.02.2024",   // такого дня нет
        "01.02.2024 ",  // лишний пробел в конце
        "01.02.2024 г.",// лишний текст
        "1.2.2024",     // не по шаблону dd.MM.uuuu
    };

    for (String value : values) {
      try {
        System.out.printf("%-15s -> %s%n", value, DateParser.parse(value));
      } catch (InvalidDateFieldException e) {
        System.out.printf("%-15s -> %s (field=%s, cause=%s)%n",
            value, e.getMessage(), e.field(), e.getCause().getClass().getSimpleName());
      }
    }
  }
}
