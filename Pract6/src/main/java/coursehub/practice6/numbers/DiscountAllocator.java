package coursehub.practice6.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

// делим сумму между позициями так, чтобы сумма долей точно совпала с исходной
public final class DiscountAllocator {
  private static final int SCALE = 2;
  private static final BigDecimal CENT = new BigDecimal("0.01");

  private DiscountAllocator() {
  }

  // режим округления передаётся явно, скрытых правил нет
  public static BigDecimal[] allocate(BigDecimal amount, int[] weights, RoundingMode mode) {
    int totalWeight = 0;
    for (int weight : weights) {
      totalWeight += weight;
    }

    BigDecimal[] shares = new BigDecimal[weights.length];
    BigDecimal distributed = BigDecimal.ZERO.setScale(SCALE);
    for (int i = 0; i < weights.length; i++) {
      BigDecimal share = amount
          .multiply(BigDecimal.valueOf(weights[i]))
          .divide(BigDecimal.valueOf(totalWeight), SCALE, mode);
      shares[i] = share;
      distributed = distributed.add(share);
    }

    // остаток копеек раскидываем по позициям по кругу — порядок детерминирован
    BigDecimal remainder = amount.setScale(SCALE, mode).subtract(distributed);
    int index = 0;
    while (remainder.signum() != 0) {
      BigDecimal step = remainder.signum() > 0 ? CENT : CENT.negate();
      shares[index] = shares[index].add(step);
      remainder = remainder.subtract(step);
      index = (index + 1) % shares.length;
    }
    return shares;
  }
}
