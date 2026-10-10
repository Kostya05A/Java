import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class CourseHubTest {
    private final CourseHub hub = new CourseHub();

    @Test
    void successImport() {
        assertEquals("OK", hub.processImport("good data"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"bad1", "bad2", "very bad"})
    void domainValidationFails(String data) {
        assertEquals("ERROR:DOMAIN_01", hub.processImport(data));
    }

    @Test
    void infrastructureErrorHandled() {
        assertEquals("ERROR:INFRA", hub.processImport(""));
    }

    @Test
    void programmingErrorPropagates() {
        assertThrows(RuntimeException.class, () -> hub.processImport(null));
    }
}
