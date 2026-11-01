package coursehub.practice6.numbers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DecimalDemoTest {
    @Test
    void doubleConstructorKeepsBinaryArtifact() {
        BigDecimal fromDouble = new BigDecimal(0.1);

        assertEquals(
                "0.1000000000000000055511151231257827021181583404541015625",
                fromDouble.toPlainString());
        assertNotEquals(new BigDecimal("0.1"), fromDouble);
    }

    @Test
    void valueOfAndStringConstructorRepresentDecimalPointOne() {
        BigDecimal fromValueOf = BigDecimal.valueOf(0.1);
        BigDecimal fromString = new BigDecimal("0.1");

        assertEquals(fromString, fromValueOf);
        assertEquals(0, fromString.compareTo(fromValueOf));
    }

    @Test
    void representationDiffersByScale() {
        BigDecimal fromDouble = new BigDecimal(0.1);
        BigDecimal fromString = new BigDecimal("0.1");

        assertEquals(55, fromDouble.stripTrailingZeros().scale());
        assertEquals(1, fromString.stripTrailingZeros().scale());
    }

    @Test
    void threeCoursesAreSummedExactly() {
        BigDecimal total = new BigDecimal("3000.00")
                .add(BigDecimal.valueOf(1500.50))
                .add(new BigDecimal("999.99"));

        assertEquals(0, total.compareTo(new BigDecimal("5500.49")));
    }
}
