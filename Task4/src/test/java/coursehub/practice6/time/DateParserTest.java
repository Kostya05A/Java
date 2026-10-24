package coursehub.practice6.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

class DateParserTest {
    @Test
    void parsesValidDate() {
        assertEquals(LocalDate.of(2024, 2, 1), DateParser.parse("01.02.2024"));
    }

    @Test
    void parsesLeapDayInLeapYear() {
        assertEquals(LocalDate.of(2024, 2, 29), DateParser.parse("29.02.2024"));
    }

    @Test
    void rejectsImpossibleDates() {
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse("29.02.2023"));
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse("31.02.2024"));
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse("31.04.2024"));
    }

    @Test
    void rejectsExtraTextAndWhitespace() {
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse("01.02.2024 "));
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse(" 01.02.2024"));
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse("01.02.2024 г."));
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse("01.02.2024\n"));
    }

    @Test
    void rejectsNonPaddedFormat() {
        assertThrows(InvalidDateFieldException.class, () -> DateParser.parse("1.2.2024"));
    }

    @Test
    void exceptionContainsFieldValueAndCause() {
        InvalidDateFieldException exception = assertThrows(
                InvalidDateFieldException.class, () -> DateParser.parse("31.02.2024"));

        assertEquals("date", exception.field());
        assertEquals("31.02.2024", exception.value());
        assertTrue(exception.getMessage().contains("date"));
        assertTrue(exception.getMessage().contains("31.02.2024"));
        assertInstanceOf(DateTimeParseException.class, exception.getCause());
    }
}
