public class StackTraceDemo {
  public static void main(String[] args) {
    a();
  }

  static void a() {
    b();
  }

  static void b() {
    c();
  }

  static void c() {
  // int x = 10/0;
  int x = 10;
  }
}
