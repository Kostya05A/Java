package task6;

public final class StatsAccumulator {
    private long count;
    private long sum;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public void accept(int value) {
        count++;
        sum += value;
        min = Math.min(min, value);
        max = Math.max(max, value);
    }

    public StatsAccumulator merge(StatsAccumulator other) {
        if (other.count == 0) {
            return this;
        }
        count += other.count;
        sum += other.sum;
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
        return this;
    }

    public Stats finish() {
        if (count == 0) {
            return new Stats(0, 0, 0, 0);
        }
        return new Stats(count, sum, min, max);
    }
}
