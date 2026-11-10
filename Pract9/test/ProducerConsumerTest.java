package task4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProducerConsumerTest {

    @Test
    @Timeout(20)
    void processesEveryRequestAndTerminatesCleanly() throws Exception {
        List<EnrollmentRequest> requests = IntStream.range(0, 1_000)
                .mapToObj(i -> new EnrollmentRequest(
                        "req-" + i,
                        "student-" + (i % 50),
                        "COURSE-" + (i % 10)))
                .toList();

        ProducerConsumerDemo.Report report =
                ProducerConsumerDemo.run(requests, 3, 4, 16, 0);

        assertEquals(requests.size(), report.produced());
        assertEquals(requests.size(), report.processed());
        assertEquals(requests.size(), report.processedIds().size());
        assertTrue(report.terminatedCleanly());
    }

    @Test
    @Timeout(20)
    void boundedQueueWorksWithSingleSlotCapacity() throws Exception {
        List<EnrollmentRequest> requests = IntStream.range(0, 200)
                .mapToObj(i -> new EnrollmentRequest(
                        "req-" + i,
                        "student-" + i,
                        "COURSE-" + (i % 5)))
                .toList();

        ProducerConsumerDemo.Report report =
                ProducerConsumerDemo.run(requests, 4, 2, 1, 0);

        assertEquals(requests.size(), report.processed());
        assertTrue(report.terminatedCleanly());
    }
}
