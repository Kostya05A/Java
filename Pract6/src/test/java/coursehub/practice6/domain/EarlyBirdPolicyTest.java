package coursehub.practice6.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class EarlyBirdPolicyTest {
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final Currency USD = Currency.getInstance("USD");
    private static final Instant FIRST = ZonedDateTime.of(
            2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();
    private static final Instant DEADLINE = FIRST.minus(Duration.ofDays(7));

    @Test
    void discountIsActiveStrictlyBeforeDeadline() {
        EarlyBirdPolicy policy = new EarlyBirdPolicy(DEADLINE, new BigDecimal("20"));

        assertTrue(policy.activeAt(DEADLINE.minusSeconds(1)));
        assertFalse(policy.activeAt(DEADLINE));
        assertFalse(policy.activeAt(DEADLINE.plusSeconds(1)));
    }

    @Test
    void appliesDiscountOnlyBeforeDeadline() {
        EarlyBirdPolicy policy = new EarlyBirdPolicy(DEADLINE, new BigDecimal("20"));
        CourseOffering offering = offering("500.00");

        assertEquals(Money.of("400.00", USD), policy.priceFor(offering, DEADLINE.minusSeconds(1)));
        assertEquals(Money.of("500.00", USD), policy.priceFor(offering, DEADLINE));
    }

    @Test
    void zeroDiscountKeepsFullPrice() {
        EarlyBirdPolicy policy = new EarlyBirdPolicy(DEADLINE, BigDecimal.ZERO);

        assertEquals(Money.of("500.00", USD),
                policy.priceFor(offering("500.00"), DEADLINE.minusSeconds(1)));
    }

    @Test
    void rejectsInvalidPolicyValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new EarlyBirdPolicy(null, BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class,
                () -> new EarlyBirdPolicy(DEADLINE, null));
        assertThrows(IllegalArgumentException.class,
                () -> new EarlyBirdPolicy(DEADLINE, new BigDecimal("-1")));
    }

    private static CourseOffering offering(String amount) {
        WebinarSeriesPlan plan = new WebinarSeriesPlan(
                new CourseTitle("Java Webinar Series"),
                FIRST,
                NEW_YORK,
                5,
                Duration.ofHours(2));
        return new CourseOffering(plan, Money.of(amount, USD));
    }
}
