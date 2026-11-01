package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

// возврат пропорционален числу оставшихся занятий
public final class RefundService {
  public Money refund(Purchase purchase, Instant moment) {
    WebinarSeriesPlan plan = purchase.offering().plan();
    int total = plan.sessionCount();
    int remaining = plan.remainingSessions(moment);

    if (remaining <= 0) {
      return Money.of("0", purchase.paid().currency());
    }

    // доля оставшихся занятий; итоговое округление делает Money.percentage
    BigDecimal percent = BigDecimal.valueOf(remaining)
        .multiply(BigDecimal.valueOf(100))
        .divide(BigDecimal.valueOf(total), 6, RoundingMode.HALF_UP);
    return purchase.paid().percentage(percent);
  }
}
