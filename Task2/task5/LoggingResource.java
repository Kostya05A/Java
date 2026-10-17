class LoggingResource implements AutoCloseable {
  private final boolean failOnUse;
  private final boolean failOnClose;

  LoggingResource(boolean failOnUse, boolean failOnClose) {
    this.failOnUse = failOnUse;
    this.failOnClose = failOnClose;
    System.out.println("open");
  }

  void use() {
    System.out.println("use");
    if (failOnUse) {
      throw new RuntimeException("use failed");
    }
  }

  @Override
  public void close() {
    System.out.println("close");
    if (failOnClose) {
      throw new RuntimeException("close failed");
    }
  }
}
