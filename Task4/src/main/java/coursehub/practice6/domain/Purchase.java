package coursehub.practice6.domain;

import java.time.Instant;

// факт покупки: что купили, сколько заплатили и когда
public record Purchase(CourseOffering offering, Money paid, Instant purchasedAt) {
  public Purchase {
    if (offering == null || paid == null || purchasedAt == null) {
      throw new IllegalArgumentException("purchase fields must not be null");
    }
  }
}
