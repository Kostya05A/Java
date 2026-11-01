package coursehub.practice6.numbers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntegerPolicyTest {
    @Test
    void unboxingNullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> IntegerPolicy.plusOneUnsafe(null));
    }

    @Test
    void explicitContractMapsNullToZero() {
        assertEquals(0, IntegerPolicy.plusOne(null));
        assertEquals(42, IntegerPolicy.plusOne(41));
    }

    @Test
    void referenceEqualityDependsOnIntegerCache() {
        Integer cachedA = 100;
        Integer cachedB = 100;
        Integer bigA = 1000;
        Integer bigB = 1000;

        assertTrue(IntegerPolicy.sameByReference(cachedA, cachedB));
        assertFalse(IntegerPolicy.sameByReference(bigA, bigB));
    }

    @Test
    void valueEqualityDoesNotDependOnCache() {
        Integer bigA = 1000;
        Integer bigB = 1000;

        assertTrue(IntegerPolicy.sameByValue(bigA, bigB));
        assertTrue(IntegerPolicy.sameByValue(null, null));
        assertFalse(IntegerPolicy.sameByValue(null, 0));
        assertFalse(IntegerPolicy.sameByValue(0, null));
    }
}
