package task2;

import java.util.Locale;

public final class TopicNormalizer {
    private TopicNormalizer() {
    }

    public static String normalize(String topic) {
        if (topic == null) {
            return "";
        }
        return topic.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
