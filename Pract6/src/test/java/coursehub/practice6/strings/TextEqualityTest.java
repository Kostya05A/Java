package coursehub.practice6.strings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextEqualityTest {
    @Test
    void comparesTextByValueEvenWhenReferencesDiffer() {
        String literal = "JAVA-101";
        String copy = new String("JAVA-101");

        assertNotSame(literal, copy);
        assertTrue(TextEquality.same(literal, copy));
    }

    @Test
    void handlesNullsByContract() {
        assertTrue(TextEquality.same(null, null));
        assertFalse(TextEquality.same("JAVA-101", null));
        assertFalse(TextEquality.same(null, "JAVA-101"));
    }
}
