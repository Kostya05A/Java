package task7;

import src.Level;

public record CatalogEntry(
        String code,
        Level level,
        int durationHours,
        int completionThreshold) {
}
