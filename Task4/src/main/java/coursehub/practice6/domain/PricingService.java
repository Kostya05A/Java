package coursehub.practice6.domain;

import java.time.Instant;

// цену определяет политика early-bird на момент покупки
public final class PricingService {
  private final EarlyBirdPolicy policy;

  public PricingService(EarlyBirdPolicy policy) {
    if (policy == null) {
      throw new IllegalArgumentException("policy must not be null");
    }
    this.policy = policy;
  }

  public Purchase purchase(CourseOffering offering, Instant moment) {
    Money price = policy.priceFor(offering, moment);
    return new Purchase(offering, price, moment);
  }
}
