package coursehub.practice6.strings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringIdentityTest {
    @Test
    void literalsSharePool() {
        String a = "JAVA-101";
        String b = "JAVA-101";
        assertSame(a, b);
        assertEquals(a, b);
    }

    @Test
    void newStringCreatesDifferentReference() {
        String a = "JAVA-101";
        String c = new String("JAVA-101");
        assertNotSame(a, c);
        assertEquals(a, c);
    }

    @Test
    void compileTimeConcatSharesPool() {
        String a = "JAVA-101";
        String d = "JAVA" + "-101";
        assertSame(a, d);
    }

    @Test
    void runtimeConcatDifferentReference() {
        String a = "JAVA-101";
        String prefix = "JAVA";
        String e = prefix + "-101";
        assertNotSame(a, e);
        assertEquals(a, e);
    }

    @Test
    void internReturnsPoolReference() {
        String a = "JAVA-101";
        String prefix = "JAVA";
        String e = prefix + "-101";
        assertSame(a, e.intern());
    }
}
