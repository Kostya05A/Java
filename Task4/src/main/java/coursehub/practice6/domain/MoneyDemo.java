package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.util.Currency;

// деньги: операции без double, защита от смешения валют
public class MoneyDemo {
  public static void main(String[] args) {
    Currency rub = Currency.getInstance("RUB");
    Currency usd = Currency.getInstance("USD");

    Money base = Money.of("3000", rub);
    Money discount = base.percentage(new BigDecimal("10"));
    Money total = base.subtract(discount);
    System.out.println("base:     " + base);
    System.out.println("discount: " + discount);
    System.out.println("total:    " + total);

    // содержательное равенство: 3000 и 3000.00 — одно и то же
    System.out.println("equals by value: " + Money.of("3000.00", rub).equals(Money.of("3000", rub)));

    // распределение без потери копеек
    Money[] parts = Money.of("100.00", rub).allocate(1, 1, 1);
    Money sum = parts[0].add(parts[1]).add(parts[2]);
    System.out.println("parts: " + parts[0] + ", " + parts[1] + ", " + parts[2]);
    System.out.println("sum exact: " + sum.equals(Money.of("100.00", rub)));

    // смешение валют — ошибка
    try {
      base.add(Money.of("10", usd));
    } catch (IllegalArgumentException e) {
      System.out.println("guard: " + e.getMessage());
    }
  }
}
