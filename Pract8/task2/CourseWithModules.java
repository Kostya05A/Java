package task2;

import src.Level;

import java.util.List;

public record CourseWithModules(String code, String title, Level level, List<Module> modules) {
    public CourseWithModules {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code не может быть пустым");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title не может быть пустым");
        }
        if (level == null) {
            throw new IllegalArgumentException("level не может быть null");
        }
        modules = List.copyOf(modules);
    }
}
