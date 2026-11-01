package coursehub.practice6.strings;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportBuilderTest {
    @Test
    void plusAndBuilderGiveSameResult() {
        List<String> lines = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            lines.add("row " + i);
        }
        String byPlus = ReportBuilder.withPlus(lines);
        String byBuilder = ReportBuilder.withBuilder(lines);
        assertEquals(byPlus, byBuilder);
        assertTrue(byBuilder.endsWith("row 1000\n"));
    }
}
