package level5;

import java.nio.file.Path;

import domain.ErrorCode;

public class SourceUnavailableException extends Exception {

    private final Path file;
    private final ErrorCode errorCode;

    public SourceUnavailableException(Path file, String message, ErrorCode errorCode, Throwable cause) {
        super(message, cause);
        this.file = file;
        this.errorCode = errorCode;
    }

    public Path file() {
        return file;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "SourceUnavailableException[file=" + file
                + ", errorCode=" + errorCode
                + ", hasCause=" + (getCause() != null) + "]";
    }
}
