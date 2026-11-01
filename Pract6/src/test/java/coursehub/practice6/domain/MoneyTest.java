package coursehub.practice6.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency JPY = Currency.getInstance("JPY");

    @Test
    void storesAmountAtCurrencyScale() {
        Money money = Money.of("3000", USD);

        assertEquals(new BigDecimal("3000.00"), money.amount());
        assertEquals(2, money.amount().scale());
        assertEquals(USD, money.currency());
    }

    @Test
    void equalityIgnoresScaleButNotCurrency() {
        Money left = Money.of("3000.00", USD);
        Money right = Money.of("3000", USD);

        assertEquals(left, right);
        assertEquals(left.hashCode(), right.hashCode());
        assertNotEquals(left, Money.of("3000", EUR));
    }

    @Test
    void addAndSubtractKeepOriginalImmutable() {
        Money base = Money.of("3000.00", USD);

        Money total = base.add(Money.of("250.50", USD));
        Money remaining = base.subtract(Money.of("300.00", USD));

        assertEquals(Money.of("3250.50", USD), total);
        assertEquals(Money.of("2700.00", USD), remaining);
        assertEquals(Money.of("3000.00", USD), base);
    }

    @Test
    void preventsCurrencyMixing() {
        Money usd = Money.of("100.00", USD);
        Money eur = Money.of("100.00", EUR);

        assertThrows(IllegalArgumentException.class, () -> usd.add(eur));
        assertThrows(IllegalArgumentException.class, () -> usd.subtract(eur));
    }

    @Test
    void percentageUsesExplicitRounding() {
        Money result = Money.of("100.00", USD).percentage(new BigDecimal("33.333"));

        assertEquals(Money.of("33.33", USD), result);
    }

    @Test
    void allocationIsExactAndDeterministic() {
        Money amount = Money.of("100.00", USD);

        Money[] first = amount.allocate(1, 1, 1);
        Money[] second = amount.allocate(1, 1, 1);

        assertMoney(first[0], "33.34");
        assertMoney(first[1], "33.33");
        assertMoney(first[2], "33.33");
        assertArrayEquals(first, second);
        assertEquals(amount, first[0].add(first[1]).add(first[2]));
    }

    @Test
    void allocationHandlesSingleCent() {
        Money[] parts = Money.of("0.01", USD).allocate(1, 1);

        assertMoney(parts[0], "0.01");
        assertMoney(parts[1], "0.00");
    }

    @Test
    void zeroDecimalCurrencyHasNoFractionalPart() {
        assertEquals(new BigDecimal("1000"), Money.of("1000.49", JPY).amount());
        assertEquals(new BigDecimal("1001"), Money.of("1000.50", JPY).amount());
        assertEquals(0, Money.of("1000.50", JPY).amount().scale());
    }

    @Test
    void rejectsNullAmountOrCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of((BigDecimal) null, USD));
        assertThrows(IllegalArgumentException.class,
                () -> Money.of("1.00", null));
    }

    private static void assertMoney(Money actual, String expectedAmount) {
        assertEquals(0, actual.amount().compareTo(new BigDecimal(expectedAmount)),
                "expected " + expectedAmount + " but was " + actual.amount());
    }
}
