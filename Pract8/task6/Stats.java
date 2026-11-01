package task6;

public record Stats(long count, long sum, int min, int max) {
    public double average() {
        return count == 0 ? 0.0 : (double) sum / count;
    }
}
