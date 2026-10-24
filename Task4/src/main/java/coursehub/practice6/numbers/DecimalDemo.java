package coursehub.practice6.numbers;

import java.math.BigDecimal;

// три способа получить 0.1 — результат разный
public class DecimalDemo {
  public static void main(String[] args) {
    // double-конструктор берёт точное двоичное значение, а не «0.1»
    BigDecimal fromDouble = new BigDecimal(0.1);
    // valueOf округляет через Double.toString
    BigDecimal valueOf = BigDecimal.valueOf(0.1);
    // строковый конструктор читает десятичную запись как есть
    BigDecimal fromString = new BigDecimal("0.1");

    System.out.println("new BigDecimal(0.1): " + fromDouble);
    System.out.println("BigDecimal.valueOf(0.1): " + valueOf);
    System.out.println("new BigDecimal(\"0.1\"): " + fromString);

    // valueOf и строковый конструктор дают одно и то же значение
    System.out.println("valueOf.equals(string): " + valueOf.equals(fromString));
    // double-конструктор даёт более длинное «хвостатое» число
    System.out.println("fromDouble.equals(string): " + fromDouble.equals(fromString));
    System.out.println("fromDouble.stripTrailingZeros scale: " + fromDouble.stripTrailingZeros().scale());
    System.out.println("fromString.stripTrailingZeros scale: " + fromString.stripTrailingZeros().scale());

    // стоимость трёх курсов: деньги считаем в BigDecimal, не в double
    BigDecimal java = new BigDecimal("3000.00");
    BigDecimal go = BigDecimal.valueOf(1500.50);
    BigDecimal sql = new BigDecimal("999.99");
    BigDecimal total = java.add(go).add(sql);
    System.out.println("three courses total: " + total);
  }
}
