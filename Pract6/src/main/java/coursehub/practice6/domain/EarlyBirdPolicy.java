package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.time.Instant;

// скидка early-bird действует строго до момента deadline
public record EarlyBirdPolicy(Instant deadline, BigDecimal discountPercent) {
  public EarlyBirdPolicy {
    if (deadline == null || discountPercent == null) {
      throw new IllegalArgumentException("deadline and discountPercent must not be null");
    }
    if (discountPercent.signum() < 0) {
      throw new IllegalArgumentException("discountPercent must be >= 0");
    }
  }

  public boolean activeAt(Instant moment) {
    return moment.isBefore(deadline);
  }

  // на границе скидка уже не действует: сравнение строгое
  public Money priceFor(CourseOffering offering, Instant moment) {
    Money full = offering.fullPrice();
    if (!activeAt(moment)) {
      return full;
    }
    return full.subtract(full.percentage(discountPercent));
  }
}
