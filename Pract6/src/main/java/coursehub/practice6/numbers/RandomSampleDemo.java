package coursehub.practice6.numbers;

import java.util.Arrays;
import java.util.random.RandomGenerator;

// одинаковый seed -> одинаковая выборка, разный -> разная
public class RandomSampleDemo {
  public static void main(String[] args) {
    RandomGenerator first = RandomSampleService.seeded(42);
    RandomGenerator second = RandomSampleService.seeded(42);
    int[] a = RandomSampleService.sample(first, 10, 1, 100);
    int[] b = RandomSampleService.sample(second, 10, 1, 100);

    System.out.println("sample: " + Arrays.toString(a));
    System.out.println("same seed -> same sample: " + Arrays.equals(a, b));

    RandomGenerator other = RandomSampleService.seeded(43);
    int[] c = RandomSampleService.sample(other, 10, 1, 100);
    System.out.println("different seed -> same sample: " + Arrays.equals(a, c));

    System.out.println("stats: " + SampleStats.of(a));
  }
}
