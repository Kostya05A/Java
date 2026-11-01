package coursehub.practice6.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

// проверяем, что сумма долей ровно равна ожидаемой
public class DiscountAllocatorDemo {
  public static void main(String[] args) {
    BigDecimal discount = new BigDecimal("100.00");
    int[] weights = {1, 1, 1};

    BigDecimal[] shares = DiscountAllocator.allocate(discount, weights, RoundingMode.HALF_UP);

    BigDecimal sum = BigDecimal.ZERO;
    for (BigDecimal share : shares) {
      System.out.println("share: " + share);
      sum = sum.add(share);
    }
    System.out.println("sum: " + sum);
    System.out.println("exact: " + discount.equals(sum));

    // неравные веса: скидка пропорциональна цене позиции
    BigDecimal[] byPrice = DiscountAllocator.allocate(
        new BigDecimal("10.00"), new int[] {3, 1}, RoundingMode.HALF_UP);
    System.out.println("by price: " + byPrice[0] + ", " + byPrice[1]);
  }
}
