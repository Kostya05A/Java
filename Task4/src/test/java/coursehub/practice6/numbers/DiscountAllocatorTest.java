package coursehub.practice6.numbers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class DiscountAllocatorTest {
    @Test
    void distributesRemainderAndKeepsExactTotal() {
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal[] shares = DiscountAllocator.allocate(
                amount, new int[] {1, 1, 1}, RoundingMode.HALF_UP);

        assertAmounts(shares, "33.34", "33.33", "33.33");
        assertEquals(0, sum(shares).compareTo(amount));
    }

    @Test
    void explicitRoundingModeChangesDistribution() {
        BigDecimal amount = new BigDecimal("10.01");
        int[] weights = {1, 1, 1};

        BigDecimal[] halfUp = DiscountAllocator.allocate(amount, weights, RoundingMode.HALF_UP);
        BigDecimal[] down = DiscountAllocator.allocate(amount, weights, RoundingMode.DOWN);

        assertAmounts(halfUp, "3.33", "3.34", "3.34");
        assertAmounts(down, "3.34", "3.34", "3.33");
        assertEquals(0, sum(halfUp).compareTo(amount));
        assertEquals(0, sum(down).compareTo(amount));
    }

    @Test
    void respectsWeights() {
        BigDecimal[] shares = DiscountAllocator.allocate(
                new BigDecimal("10.00"), new int[] {3, 1}, RoundingMode.HALF_UP);

        assertAmounts(shares, "7.50", "2.50");
    }

    @Test
    void resultIsDeterministic() {
        BigDecimal amount = new BigDecimal("100.00");
        int[] weights = {1, 1, 1};

        BigDecimal[] first = DiscountAllocator.allocate(amount, weights, RoundingMode.HALF_UP);
        BigDecimal[] second = DiscountAllocator.allocate(amount, weights, RoundingMode.HALF_UP);

        assertArrayEquals(first, second);
    }

    private static BigDecimal sum(BigDecimal[] values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            total = total.add(value);
        }
        return total;
    }

    private static void assertAmounts(BigDecimal[] actual, String... expected) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(0, actual[i].compareTo(new BigDecimal(expected[i])),
                    "share " + i + " expected " + expected[i] + " but was " + actual[i]);
        }
    }
}
