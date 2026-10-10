import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
class FragileTestFix {
    
    @Test
    void testIndependentState() {
        StringBuilder localState = new StringBuilder("test");
        localState.append("_data");
        assertTrue(localState.toString().contains("test"));
    }

    @Test
    void testNoDirectoryDependency() {
        String path = "src/data.txt";
        assertTrue(path.endsWith(".txt"));
    }

    @Test
    void testFlexibleToString() {
        Object obj = new Object() {
            @Override
            public String toString() {
                return "User{id=1, name=John, role=admin}";
            }
        };
        String result = obj.toString();
        assertTrue(result.contains("name=John"));
        assertTrue(result.contains("role=admin"));
    }
}
