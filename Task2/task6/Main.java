public class Main {
  public static void main(String[] args) throws Exception {
    src.Course c = ParseRecord.parseRecord("42", 1);
    System.out.println(c.durationHours());
  }
}
