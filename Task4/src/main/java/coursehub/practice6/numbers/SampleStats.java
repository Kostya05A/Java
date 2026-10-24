package coursehub.practice6.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

// границы выборки и округлённое среднее
public record SampleStats(int min, int max, BigDecimal average) {
  public static SampleStats of(int[] values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("empty sample");
    }
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    long sum = 0;
    for (int value : values) {
      min = Math.min(min, value);
      max = Math.max(max, value);
      sum += value;
    }
    // среднее округляем явно, а не через неявное деление целых
    BigDecimal average = BigDecimal.valueOf(sum)
        .divide(BigDecimal.valueOf(values.length), 2, RoundingMode.HALF_UP);
    return new SampleStats(min, max, average);
  }
}
