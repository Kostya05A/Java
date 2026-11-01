package task2;

import java.util.List;

public record Module(String name, List<String> topics) {
    public Module {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name модуля не может быть пустым");
        }
        topics = List.copyOf(topics);
    }
}
