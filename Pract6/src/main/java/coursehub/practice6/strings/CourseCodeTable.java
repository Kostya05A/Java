package coursehub.practice6.strings;

// таблица валидных и невалидных значений: Unicode, пробелы, частичное совпадение
public class CourseCodeTable {
  public static void main(String[] args) {
    String[] values = {
        "JAVA-101",
        "GO-7",
        "JAVA-101x",   // лишний символ — частичное совпадение запрещено
        "xJAVA-101",   // лишний префикс
        " JAVA-101",   // ведущий пробел
        "JAVA-101 ",   // хвостовой пробел
        "java-101",    // строчные буквы
        "ЯВА-101",     // кириллица: [A-Z] её не принимает
        "JAVA 101",    // пробел вместо дефиса
        "JAVA-",       // нет номера
    };

    for (String value : values) {
      CourseCode.parse(value).ifPresentOrElse(
          code -> System.out.printf("%-12s -> valid   (%s, %d)%n", value, code.prefix(), code.number()),
          () -> System.out.printf("%-12s -> invalid%n", value));
    }
  }
}
