package coursehub.practice6.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DstResolverTest {
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    @Test
    void gapIsMovedForwardByTheSizeOfTheSpringJump() {
        LocalDateTime missing = LocalDateTime.of(2024, 3, 10, 2, 30);

        ZonedDateTime resolved = LocalDateTimeResolver.resolveGapForward(missing, NEW_YORK);

        assertEquals(LocalDateTime.of(2024, 3, 10, 3, 30), resolved.toLocalDateTime());
        assertEquals(ZoneOffset.ofHours(-4), resolved.getOffset());
    }

    @Test
    void strictPolicyRejectsMissingLocalTime() {
        LocalDateTime missing = LocalDateTime.of(2024, 3, 10, 2, 30);

        InvalidLocalTimeException exception = assertThrows(
                InvalidLocalTimeException.class,
                () -> LocalDateTimeResolver.resolveStrict(missing, NEW_YORK));

        assertEquals(missing, exception.localTime());
        assertEquals(NEW_YORK, exception.zone());
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void sessionPolicyRejectsMissingLocalTime() {
        LocalDateTime missing = LocalDateTime.of(2024, 3, 10, 2, 30);

        assertThrows(
                InvalidLocalTimeException.class,
                () -> LocalDateTimeResolver.resolveSessionStart(missing, NEW_YORK));
    }

    @Test
    void sessionPolicyAcceptsExistingLocalTime() {
        LocalDateTime existing = LocalDateTime.of(2024, 3, 10, 3, 30);

        ZonedDateTime resolved = LocalDateTimeResolver.resolveSessionStart(existing, NEW_YORK);

        assertEquals(existing, resolved.toLocalDateTime());
    }

    @Test
    void overlapExposesEarlierAndLaterOffsets() {
        LocalDateTime repeated = LocalDateTime.of(2024, 11, 3, 1, 30);

        ZonedDateTime earlier = repeated.atZone(NEW_YORK);
        ZonedDateTime later = LocalDateTimeResolver.laterOffsetAtOverlap(repeated, NEW_YORK);

        assertEquals(ZoneOffset.ofHours(-4), earlier.getOffset());
        assertEquals(ZoneOffset.ofHours(-5), later.getOffset());
        assertEquals(earlier.toLocalDateTime(), later.toLocalDateTime());
        assertTrue(later.toInstant().isAfter(earlier.toInstant()));
    }

    @Test
    void normalLocalTimeResolvesUnchanged() {
        LocalDateTime normal = LocalDateTime.of(2024, 3, 11, 12, 0);

        ZonedDateTime resolved = LocalDateTimeResolver.resolveStrict(normal, NEW_YORK);

        assertEquals(normal, resolved.toLocalDateTime());
    }
}
