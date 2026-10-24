package coursehub.practice6.numbers;

// демонстрация: NPE, ловушка == и корректный контракт
public class AutoboxingDemo {
  public static void main(String[] args) {
    // NPE при unboxing null
    try {
      IntegerPolicy.plusOneUnsafe(null);
    } catch (NullPointerException e) {
      System.out.println("NPE: unboxing null");
    }
    System.out.println("safe plusOne(null): " + IntegerPolicy.plusOne(null));

    // кэш -128..127: 100 == 100 истинно, 1000 == 1000 ложно
    Integer cachedA = 100;
    Integer cachedB = 100;
    Integer bigA = 1000;
    Integer bigB = 1000;
    System.out.println("100 == 100 (ref): " + IntegerPolicy.sameByReference(cachedA, cachedB));
    System.out.println("1000 == 1000 (ref): " + IntegerPolicy.sameByReference(bigA, bigB));
    System.out.println("1000 equals 1000: " + IntegerPolicy.sameByValue(bigA, bigB));
  }
}
