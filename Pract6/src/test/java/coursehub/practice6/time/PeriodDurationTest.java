package coursehub.practice6.time;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PeriodDurationTest {
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    @Test
    void calendarDayAndExact24HoursDivergeAcrossDst() {
        Instant start = ZonedDateTime.of(2024, 3, 10, 0, 30, 0, 0, NEW_YORK).toInstant();
        WebinarSchedule schedule = new WebinarSchedule(start);

        ZonedDateTime calendarDay = schedule.plusCalendarDays(1, NEW_YORK);
        Instant exact24Hours = schedule.plusExactHours(24);

        assertEquals(LocalDateTime.of(2024, 3, 11, 0, 30), calendarDay.toLocalDateTime());
        assertEquals(23, Duration.between(start, calendarDay.toInstant()).toHours());
        assertEquals(24, Duration.between(start, exact24Hours).toHours());
        assertNotEquals(calendarDay.toInstant(), exact24Hours);
        assertEquals(start, schedule.start());
    }

    @Test
    void sameInstantIsDisplayedInThreeZones() {
        Instant start = ZonedDateTime.of(2024, 3, 6, 18, 0, 0, 0, NEW_YORK).toInstant();
        WebinarSchedule schedule = new WebinarSchedule(start);

        List<String> display = schedule.displayAll(
                ZoneId.of("UTC"),
                ZoneId.of("Europe/Berlin"),
                ZoneId.of("Asia/Tokyo"));

        assertEquals(
                List.of(
                        "UTC -> 2024-03-06T23:00",
                        "Europe/Berlin -> 2024-03-07T00:00",
                        "Asia/Tokyo -> 2024-03-07T08:00"),
                display);
        assertEquals(start, schedule.start());
        assertEquals(start, schedule.displayIn(ZoneId.of("Asia/Tokyo")).toInstant());
    }

    @Test
    void rejectsNullStart() {
        assertThrows(IllegalArgumentException.class, () -> new WebinarSchedule(null));
    }
}
