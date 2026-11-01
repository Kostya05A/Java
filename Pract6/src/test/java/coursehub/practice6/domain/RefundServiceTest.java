package coursehub.practice6.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RefundServiceTest {
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final Currency USD = Currency.getInstance("USD");
    private static final Instant FIRST = ZonedDateTime.of(
            2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();

    @Test
    void refundsProportionallyToRemainingSessions() {
        WebinarSeriesPlan plan = plan(5);
        Purchase purchase = discountedPurchase(plan);
        List<Instant> starts = plan.sessionStarts();

        Money refund = new RefundService().refund(
                purchase, starts.get(1).plus(Duration.ofHours(3)));

        assertEquals(3, plan.remainingSessions(starts.get(1).plus(Duration.ofHours(3))));
        assertEquals(Money.of("240.00", USD), refund);
    }

    @Test
    void refundsEverythingBeforeFirstSession() {
        WebinarSeriesPlan plan = plan(5);
        Purchase purchase = discountedPurchase(plan);

        Money refund = new RefundService().refund(purchase, FIRST.minusSeconds(1));

        assertEquals(Money.of("400.00", USD), refund);
    }

    @Test
    void refundsNothingAfterLastSessionStarts() {
        WebinarSeriesPlan plan = plan(5);
        Purchase purchase = discountedPurchase(plan);

        Money refund = new RefundService().refund(
                purchase, plan.sessionStarts().get(4));

        assertEquals(Money.of("0.00", USD), refund);
    }

    @Test
    void roundsFinalRefundExplicitly() {
        WebinarSeriesPlan plan = plan(3);
        CourseOffering offering = new CourseOffering(plan, Money.of("100.00", USD));
        Purchase purchase = new Purchase(offering, Money.of("100.00", USD), FIRST);

        Money refund = new RefundService().refund(
                purchase, plan.sessionStarts().get(0));

        assertEquals(2, plan.remainingSessions(plan.sessionStarts().get(0)));
        assertEquals(Money.of("66.67", USD), refund);
    }

    private static WebinarSeriesPlan plan(int count) {
        return new WebinarSeriesPlan(
                new CourseTitle("Java Webinar Series"),
                FIRST,
                NEW_YORK,
                count,
                Duration.ofHours(2));
    }

    private static Purchase discountedPurchase(WebinarSeriesPlan plan) {
        CourseOffering offering = new CourseOffering(plan, Money.of("500.00", USD));
        EarlyBirdPolicy policy = new EarlyBirdPolicy(
                FIRST.minus(Duration.ofDays(7)), new BigDecimal("20"));
        return new PricingService(policy).purchase(
                offering, FIRST.minus(Duration.ofDays(8)));
    }
}
