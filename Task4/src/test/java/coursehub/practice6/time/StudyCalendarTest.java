package coursehub.practice6.time;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudyCalendarTest {
    @Test
    void calculatesModuleStartsWithoutMutatingSourceDate() {
        StudyCalendar calendar = new StudyCalendar(LocalDate.of(2024, 2, 5));

        assertEquals(
                List.of(
                        LocalDate.of(2024, 2, 5),
                        LocalDate.of(2024, 3, 4),
                        LocalDate.of(2024, 4, 1)),
                calendar.moduleStarts(3, 4));
        assertEquals(LocalDate.of(2024, 2, 5), calendar.start());
    }

    @Test
    void returnsFridayWhenLastDayFallsOnWeekend() {
        StudyCalendar calendar = new StudyCalendar(LocalDate.of(2024, 2, 5));

        assertEquals(LocalDate.of(2024, 4, 26), calendar.lastStudyDay(12));
        assertEquals(LocalDate.of(2024, 2, 5), calendar.start());
    }

    @Test
    void keepsWeekdayLastDayAsIs() {
        StudyCalendar calendar = new StudyCalendar(LocalDate.of(2024, 2, 7));

        assertEquals(LocalDate.of(2024, 2, 13), calendar.lastStudyDay(1));
    }

    @Test
    void detectsLeapYear() {
        assertTrue(new StudyCalendar(LocalDate.of(2024, 2, 5)).isLeapYear());
        assertFalse(new StudyCalendar(LocalDate.of(2023, 2, 5)).isLeapYear());
    }

    @Test
    void emptyModuleListIsAllowed() {
        StudyCalendar calendar = new StudyCalendar(LocalDate.of(2024, 2, 5));

        assertTrue(calendar.moduleStarts(0, 4).isEmpty());
    }

    @Test
    void rejectsNullStart() {
        assertThrows(IllegalArgumentException.class, () -> new StudyCalendar(null));
    }
}
