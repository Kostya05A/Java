package coursehub.practice6.numbers;

// контракт работы с Integer: явно решаем, что делать с null
public final class IntegerPolicy {
  private IntegerPolicy() {
  }

  // unboxing без проверки: null приводит к NullPointerException
  public static int plusOneUnsafe(Integer value) {
    return value + 1;
  }

  // == сравнивает ссылки; для Integer вне кэша (-128..127) это разные объекты
  public static boolean sameByReference(Integer left, Integer right) {
    return left == right;
  }

  // явный контракт: null заменяется на 0
  public static int plusOne(Integer value) {
    return value == null ? 0 : value + 1;
  }

  // содержательное равенство не зависит от кэша wrapper-объектов
  public static boolean sameByValue(Integer left, Integer right) {
    return java.util.Objects.equals(left, right);
  }
}
