package level2;

import java.io.IOException;

public final class RawNumberParser {

    private RawNumberParser() {
    }

    public static int parseHours(String text) {
        String value = text.startsWith("hours=") ? text.substring("hours=".length()) : text;

        return Integer.parseInt(value.trim());
    }

    public static int parseHoursOrThrowIo(String text) throws IOException {
        try {
            return parseHours(text);
        } catch (NumberFormatException ex) {
            throw new IOException("поле hours повреждено", ex);
        }
    }
}
