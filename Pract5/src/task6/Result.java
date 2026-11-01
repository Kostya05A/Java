package task6;

public final class Result<T> {
    private final T value;
    private final String error;
    private final boolean ok;

    private Result(T value, String error, boolean ok) {
        this.value = value;
        this.error = error;
        this.ok = ok;
        // инвариант
        if (ok && value == null) throw new IllegalStateException("success with null value");
        if (!ok && error == null) throw new IllegalStateException("failure with null error");
    }

    public static <T> Result<T> success(T value) { return new Result<>(value, null, true); }
    public static <T> Result<T> failure(String error) { return new Result<>(null, error, false); }

    public boolean isOk() { return ok; }
    public T getValue() {
        if (!ok) throw new IllegalStateException("not a success");
        return value;
    }
    public String getError() {
        if (ok) throw new IllegalStateException("not a failure");
        return error;
    }

    public <U> Result<U> map(java.util.function.Function<? super T, ? extends U> f) {
        return ok ? success(f.apply(value)) : failure(error);
    }
}
