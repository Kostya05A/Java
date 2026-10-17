public class Main {
  public static void main(String[] args) {
    try {
      try (LoggingResource r = new LoggingResource(true, true)) {
        r.use();
      }
    } catch (RuntimeException e) {
      System.out.println("primary = " + e.getMessage());
      for (Throwable suppressed : e.getSuppressed()) {
        System.out.println("suppressed = " + suppressed.getMessage());
      }
    }
  }
}
