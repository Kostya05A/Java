package coursehub.practice6.numbers;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RandomSampleServiceTest {
    @Test
    void sameSeedProducesSameSample() {
        int[] first = RandomSampleService.sample(
                RandomSampleService.seeded(42), 20, 1, 100);
        int[] second = RandomSampleService.sample(
                RandomSampleService.seeded(42), 20, 1, 100);

        assertArrayEquals(first, second);
    }

    @Test
    void differentSeedProducesDifferentSample() {
        int[] first = RandomSampleService.sample(
                RandomSampleService.seeded(42), 20, 1, 100);
        int[] other = RandomSampleService.sample(
                RandomSampleService.seeded(43), 20, 1, 100);

        assertFalse(Arrays.equals(first, other));
    }

    @Test
    void valuesStayWithinHalfOpenRange() {
        int[] values = RandomSampleService.sample(
                RandomSampleService.seeded(7), 100, 1, 10);

        for (int value : values) {
            assertTrue(value >= 1 && value < 10, "out of range: " + value);
        }
    }

    @Test
    void productionGeneratorIsAvailable() {
        assertNotNull(RandomSampleService.production());
        assertEquals(5, RandomSampleService.sample(
                RandomSampleService.production(), 5, 0, 10).length);
    }

    @Test
    void statsContainBoundsAndRoundedAverage() {
        SampleStats stats = SampleStats.of(new int[] {1, 2, 2});

        assertEquals(1, stats.min());
        assertEquals(2, stats.max());
        assertEquals(new BigDecimal("1.67"), stats.average());
    }

    @Test
    void statsUseLongSumToAvoidOverflow() {
        SampleStats stats = SampleStats.of(new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE});

        assertEquals(new BigDecimal("2147483647.00"), stats.average());
    }

    @Test
    void statsRejectEmptySample() {
        assertThrows(IllegalArgumentException.class, () -> SampleStats.of(new int[0]));
    }
}
