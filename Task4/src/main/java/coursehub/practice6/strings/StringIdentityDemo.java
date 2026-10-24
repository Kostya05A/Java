package coursehub.practice6.strings;

// == сравнивает ссылки, equals — содержимое
public class StringIdentityDemo {
  public static void main(String[] args) {
    // литералы: обе ссылки указывают на один объект из string pool
    String a = "JAVA-101";
    String b = "JAVA-101";
    System.out.println("literal == literal: " + (a == b));
    System.out.println("literal.equals(literal): " + a.equals(b));

    // new String всегда создаёт новый объект вне pool
    String c = new String("JAVA-101");
    System.out.println("literal == new String: " + (a == c));
    System.out.println("literal.equals(new String): " + a.equals(c));

    // compile-time конкатенация литералов сворачивается компилятором
    String d = "JAVA" + "-101";
    System.out.println("literal == compile-time concat: " + (a == d));

    // runtime конкатенация даёт новый объект
    String prefix = "JAVA";
    String e = prefix + "-101";
    System.out.println("literal == runtime concat: " + (a == e));

    // intern возвращает каноническую ссылку из pool
    System.out.println("literal == interned: " + (a == e.intern()));
  }
}
