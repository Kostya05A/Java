package coursehub.practice6.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;

// полный сквозной сценарий: зоны, DST, leap day, граница скидки, возврат, валюты
public class PricingDemo {
  public static void main(String[] args) {
    ZoneId organizer = ZoneId.of("America/New_York");
    Currency usd = Currency.getInstance("USD");

    // 5 занятий по средам в 18:00 по Нью-Йорку, старт до перехода на летнее время
    Instant first = ZonedDateTime.of(2024, 3, 6, 18, 0, 0, 0, organizer).toInstant();
    WebinarSeriesPlan plan = new WebinarSeriesPlan(
        new CourseTitle("Java Webinar Series"), first, organizer, 5, Duration.ofHours(2));
    CourseOffering offering = new CourseOffering(plan, Money.of("500.00", usd));

    // DST: местное время занятия не съезжает
    System.out.println("organizer local times:");
    for (Instant start : plan.sessionStarts()) {
      System.out.println("  " + start.atZone(organizer));
    }
    // пользователь в другой зоне видит свой час, но тот же момент
    System.out.println("viewer in Berlin: " + plan.firstSessionFor(ZoneId.of("Europe/Berlin")));

    // граница скидки: ровно в deadline скидка уже не действует
    Instant deadline = first.minus(Duration.ofDays(7));
    EarlyBirdPolicy earlyBird = new EarlyBirdPolicy(deadline, new BigDecimal("20"));
    PricingService pricing = new PricingService(earlyBird);
    Purchase discounted = pricing.purchase(offering, deadline.minusSeconds(1));
    Purchase full = pricing.purchase(offering, deadline);
    System.out.println("before deadline: " + discounted.paid());
    System.out.println("at deadline:     " + full.paid());

    // возврат: после второго занятия впереди ещё три из пяти
    Instant afterSecond = plan.sessionStarts().get(1).plus(Duration.ofHours(3));
    Money refund = new RefundService().refund(discounted, afterSecond);
    System.out.println("remaining: " + plan.remainingSessions(afterSecond) + " -> refund " + refund);

    // високосный день: одно из занятий приходится на 29 февраля
    Instant leap = ZonedDateTime.of(2024, 2, 29, 18, 0, 0, 0, organizer).toInstant();
    WebinarSeriesPlan leapPlan = new WebinarSeriesPlan(
        new CourseTitle("Leap Series"), leap, organizer, 2, Duration.ofHours(1));
    System.out.println("contains leap day: " + leapPlan.containsLeapDay());

    // другие валюты: цена считается в валюте предложения, смешивать нельзя
    Currency rub = Currency.getInstance("RUB");
    CourseOffering rubOffer = new CourseOffering(plan, Money.of("45000", rub));
    System.out.println("rub price: " + pricing.purchase(rubOffer, deadline.minusSeconds(1)).paid());
    try {
      discounted.paid().add(rubOffer.fullPrice());
    } catch (IllegalArgumentException e) {
      System.out.println("currency guard: " + e.getMessage());
    }
  }
}
