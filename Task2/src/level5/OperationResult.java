package level5;

import domain.ErrorCode;

public record OperationResult(
        ErrorCode code,
        boolean success,
        String message
) {

    public static OperationResult success(String message) {
        return new OperationResult(ErrorCode.OK, true, message);
    }

    public static OperationResult success(ErrorCode code, String message) {
        if (code == null) {
            throw new IllegalArgumentException("код обязателен");
        }
        return new OperationResult(code, true, message);
    }

    public static OperationResult failure(ErrorCode code, String message) {
        if (code == null) {
            throw new IllegalArgumentException("у отказа обязателен код");
        }
        return new OperationResult(code, false, message);
    }

    public String userMessage() {
        return message != null ? message : code.clientMessage();
    }

    @Override
    public String toString() {
        return (success ? "OK   " : "FAIL ")
                + "[" + code + "] "
                + userMessage();
    }
}
