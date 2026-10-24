package coursehub.practice6.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebinarSeriesPlanTest {
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    @Test
    void weeklySessionsKeepLocalTimeAcrossDst() {
        Instant first = ZonedDateTime.of(2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();
        WebinarSeriesPlan plan = plan(first, NEW_YORK, 5);

        List<Instant> starts = plan.sessionStarts();

        assertEquals(5, starts.size());
        for (Instant start : starts) {
            assertEquals(LocalTime.of(18, 0), start.atZone(NEW_YORK).toLocalTime());
        }
        assertEquals(ZoneOffset.ofHours(-5), starts.get(0).atZone(NEW_YORK).getOffset());
        assertEquals(ZoneOffset.ofHours(-4), starts.get(1).atZone(NEW_YORK).getOffset());
    }

    @Test
    void remainingSessionsDependsOnCurrentMoment() {
        Instant first = ZonedDateTime.of(2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();
        WebinarSeriesPlan plan = plan(first, NEW_YORK, 5);
        List<Instant> starts = plan.sessionStarts();

        assertEquals(5, plan.remainingSessions(first.minusSeconds(1)));
        assertEquals(4, plan.remainingSessions(starts.get(0)));
        assertEquals(3, plan.remainingSessions(starts.get(1).plus(Duration.ofHours(3))));
        assertEquals(0, plan.remainingSessions(starts.get(4)));
    }

    @Test
    void displaysFirstSessionInViewerZone() {
        Instant first = ZonedDateTime.of(2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();
        WebinarSeriesPlan plan = plan(first, NEW_YORK, 5);

        ZonedDateTime berlin = plan.firstSessionFor(ZoneId.of("Europe/Berlin"));

        assertEquals(LocalTime.of(0, 0), berlin.toLocalTime());
        assertEquals(first, berlin.toInstant());
    }

    @Test
    void detectsLeapDayInSchedule() {
        Instant leapDay = ZonedDateTime.of(2024, 2, 29, 18, 0, 0, 0, NEW_YORK).toInstant();
        Instant regularDay = ZonedDateTime.of(2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();

        assertTrue(plan(leapDay, NEW_YORK, 2).containsLeapDay());
        assertFalse(plan(regularDay, NEW_YORK, 2).containsLeapDay());
    }

    @Test
    void rejectsInvalidPlanArguments() {
        Instant first = Instant.parse("2024-03-06T23:00:00Z");
        CourseTitle title = new CourseTitle("Java");
        Duration duration = Duration.ofHours(2);

        assertThrows(IllegalArgumentException.class,
                () -> new WebinarSeriesPlan(null, first, NEW_YORK, 5, duration));
        assertThrows(IllegalArgumentException.class,
                () -> new WebinarSeriesPlan(title, null, NEW_YORK, 5, duration));
        assertThrows(IllegalArgumentException.class,
                () -> new WebinarSeriesPlan(title, first, null, 5, duration));
        assertThrows(IllegalArgumentException.class,
                () -> new WebinarSeriesPlan(title, first, NEW_YORK, 5, null));
        assertThrows(IllegalArgumentException.class,
                () -> new WebinarSeriesPlan(title, first, NEW_YORK, 0, duration));
        assertThrows(IllegalArgumentException.class,
                () -> new WebinarSeriesPlan(title, first, NEW_YORK, 5, Duration.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new WebinarSeriesPlan(title, first, NEW_YORK, 5, Duration.ofHours(-1)));
    }

    private static WebinarSeriesPlan plan(Instant first, ZoneId zone, int count) {
        return new WebinarSeriesPlan(
                new CourseTitle("Java Webinar Series"),
                first,
                zone,
                count,
                Duration.ofHours(2));
    }
}
