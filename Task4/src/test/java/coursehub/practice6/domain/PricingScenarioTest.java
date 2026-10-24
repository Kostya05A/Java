package coursehub.practice6.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PricingScenarioTest {
    @Test
    void endToEndCoversDstLeapDeadlineRefundAndCurrencies() {
        ZoneId organizer = ZoneId.of("America/New_York");
        Currency usd = Currency.getInstance("USD");
        Currency rub = Currency.getInstance("RUB");
        Instant first = ZonedDateTime.of(
                2024, 3, 6, 18, 0, 0, 0, organizer).toInstant();

        WebinarSeriesPlan plan = new WebinarSeriesPlan(
                new CourseTitle("Java Webinar Series"),
                first,
                organizer,
                5,
                Duration.ofHours(2));
        CourseOffering offering = new CourseOffering(plan, Money.of("500.00", usd));
        List<Instant> starts = plan.sessionStarts();

        assertEquals(LocalTime.of(18, 0), starts.get(0).atZone(organizer).toLocalTime());
        assertEquals(LocalTime.of(18, 0), starts.get(1).atZone(organizer).toLocalTime());
        assertEquals(ZoneOffset.ofHours(-5), starts.get(0).atZone(organizer).getOffset());
        assertEquals(ZoneOffset.ofHours(-4), starts.get(1).atZone(organizer).getOffset());

        Instant deadline = first.minus(Duration.ofDays(7));
        PricingService pricing = new PricingService(
                new EarlyBirdPolicy(deadline, new BigDecimal("20")));
        Purchase discounted = pricing.purchase(offering, deadline.minusSeconds(1));
        Purchase full = pricing.purchase(offering, deadline);

        assertEquals(Money.of("400.00", usd), discounted.paid());
        assertEquals(Money.of("500.00", usd), full.paid());

        Instant afterSecond = starts.get(1).plus(Duration.ofHours(3));
        Money refund = new RefundService().refund(discounted, afterSecond);

        assertEquals(3, plan.remainingSessions(afterSecond));
        assertEquals(Money.of("240.00", usd), refund);

        Instant leapDay = ZonedDateTime.of(
                2024, 2, 29, 18, 0, 0, 0, organizer).toInstant();
        WebinarSeriesPlan leapPlan = new WebinarSeriesPlan(
                new CourseTitle("Leap Series"),
                leapDay,
                organizer,
                2,
                Duration.ofHours(1));

        assertTrue(leapPlan.containsLeapDay());

        CourseOffering rubOffering = new CourseOffering(plan, Money.of("45000", rub));
        Purchase rubPurchase = pricing.purchase(rubOffering, deadline.minusSeconds(1));

        assertEquals(Money.of("36000.00", rub), rubPurchase.paid());
        assertThrows(IllegalArgumentException.class,
                () -> discounted.paid().add(rubOffering.fullPrice()));
    }
}
