package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

// неизменяемый денежный тип: сумма и валюта, без double
public final class Money {
  private final BigDecimal amount;
  private final Currency currency;

  private Money(BigDecimal amount, Currency currency) {
    if (amount == null || currency == null) {
      throw new IllegalArgumentException("amount and currency must not be null");
    }
    // явное округление до копеек валюты
    this.amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    this.currency = currency;
  }

  public static Money of(BigDecimal amount, Currency currency) {
    return new Money(amount, currency);
  }

  public static Money of(String amount, Currency currency) {
    return new Money(new BigDecimal(amount), currency);
  }

  public BigDecimal amount() {
    return amount;
  }

  public Currency currency() {
    return currency;
  }

  public Money add(Money other) {
    requireSameCurrency(other);
    return new Money(amount.add(other.amount), currency);
  }

  public Money subtract(Money other) {
    requireSameCurrency(other);
    return new Money(amount.subtract(other.amount), currency);
  }

  // смешивать валюты нельзя: это ошибка модели, а не повод молча сложить числа
  private void requireSameCurrency(Money other) {
    if (!currency.equals(other.currency)) {
      throw new IllegalArgumentException("currency mismatch: " + currency + " vs " + other.currency);
    }
  }

  // процент от суммы: деление с явным режимом округления
  public Money percentage(BigDecimal percent) {
    BigDecimal result = amount
        .multiply(percent)
        .divide(BigDecimal.valueOf(100), currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    return new Money(result, currency);
  }

  // делим сумму между позициями так, чтобы итог совпал точно
  public Money[] allocate(int... weights) {
    int totalWeight = 0;
    for (int weight : weights) {
      totalWeight += weight;
    }
    Money[] parts = new Money[weights.length];
    BigDecimal distributed = BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits());
    for (int i = 0; i < weights.length; i++) {
      BigDecimal share = amount
          .multiply(BigDecimal.valueOf(weights[i]))
          .divide(BigDecimal.valueOf(totalWeight), currency.getDefaultFractionDigits(), RoundingMode.DOWN);
      parts[i] = new Money(share, currency);
      distributed = distributed.add(share);
    }
    BigDecimal remainder = amount.subtract(distributed);
    BigDecimal cent = BigDecimal.ONE.movePointLeft(currency.getDefaultFractionDigits());
    int index = 0;
    while (remainder.signum() != 0) {
      BigDecimal step = remainder.signum() > 0 ? cent : cent.negate();
      parts[index] = new Money(parts[index].amount.add(step), currency);
      remainder = remainder.subtract(step);
      index = (index + 1) % parts.length;
    }
    return parts;
  }

  // содержательное равенство: сумма и валюта, без учёта масштаба записи
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Money other)) {
      return false;
    }
    return amount.compareTo(other.amount) == 0 && currency.equals(other.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount.stripTrailingZeros(), currency);
  }

  @Override
  public String toString() {
    return amount + " " + currency.getCurrencyCode();
  }
}
