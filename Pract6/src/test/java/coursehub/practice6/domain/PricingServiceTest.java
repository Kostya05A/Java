package coursehub.practice6.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTest {
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final Currency USD = Currency.getInstance("USD");
    private static final Instant FIRST = ZonedDateTime.of(
            2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();
    private static final Instant DEADLINE = FIRST.minus(Duration.ofDays(7));

    @Test
    void createsPurchaseWithPriceForMoment() {
        PricingService pricing = new PricingService(
                new EarlyBirdPolicy(DEADLINE, new BigDecimal("20")));
        CourseOffering offering = offering();
        Instant moment = DEADLINE.minusSeconds(1);

        Purchase purchase = pricing.purchase(offering, moment);

        assertEquals(offering, purchase.offering());
        assertEquals(Money.of("400.00", USD), purchase.paid());
        assertEquals(moment, purchase.purchasedAt());
    }

    @Test
    void usesFullPriceAtDeadline() {
        PricingService pricing = new PricingService(
                new EarlyBirdPolicy(DEADLINE, new BigDecimal("20")));

        Purchase purchase = pricing.purchase(offering(), DEADLINE);

        assertEquals(Money.of("500.00", USD), purchase.paid());
    }

    @Test
    void rejectsNullPolicy() {
        assertThrows(IllegalArgumentException.class, () -> new PricingService(null));
    }

    private static CourseOffering offering() {
        WebinarSeriesPlan plan = new WebinarSeriesPlan(
                new CourseTitle("Java Webinar Series"),
                FIRST,
                NEW_YORK,
                5,
                Duration.ofHours(2));
        return new CourseOffering(plan, Money.of("500.00", USD));
    }
}
