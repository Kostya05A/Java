package coursehub.practice6.strings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseCodeTest {
    @Test
    void extractsPrefixAndNumber() {
        CourseCode code = CourseCode.parse("JAVA-101").orElseThrow();

        assertEquals("JAVA", code.prefix());
        assertEquals(101, code.number());
    }

    @Test
    void acceptsShortPrefixAndSingleDigitNumber() {
        CourseCode code = CourseCode.parse("GO-7").orElseThrow();

        assertEquals("GO", code.prefix());
        assertEquals(7, code.number());
    }

    @Test
    void rejectsPartialMatches() {
        assertTrue(CourseCode.parse("JAVA-101x").isEmpty());
        assertTrue(CourseCode.parse("xJAVA-101").isEmpty());
        assertTrue(CourseCode.parse("JAVA-101\n").isEmpty());
    }

    @Test
    void rejectsWhitespaceAndWrongCase() {
        assertTrue(CourseCode.parse(" JAVA-101").isEmpty());
        assertTrue(CourseCode.parse("JAVA-101 ").isEmpty());
        assertTrue(CourseCode.parse("JAVA 101").isEmpty());
        assertTrue(CourseCode.parse("java-101").isEmpty());
    }

    @Test
    void rejectsUnicodeLettersAndDigits() {
        assertTrue(CourseCode.parse("ЯВА-101").isEmpty());
        assertTrue(CourseCode.parse("JAVA-\u0661\u0660\u0661").isEmpty());
    }

    @Test
    void rejectsNull() {
        assertTrue(CourseCode.parse(null).isEmpty());
    }
}
