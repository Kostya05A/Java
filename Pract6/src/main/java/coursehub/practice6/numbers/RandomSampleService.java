package coursehub.practice6.numbers;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

// отделяем продакшн-случайность от воспроизводимого источника для тестов
public final class RandomSampleService {
  private static final String ALGORITHM = "L64X128MixRandom";

  private RandomSampleService() {
  }

  // продакшн: работаем через интерфейс, не привязываясь к конкретному классу
  public static RandomGenerator production() {
    return RandomGeneratorFactory.of(ALGORITHM).create();
  }

  // тесты: seed делает последовательность воспроизводимой
  public static RandomGenerator seeded(long seed) {
    return RandomGeneratorFactory.of(ALGORITHM).create(seed);
  }

  // выборка в полуинтервале [origin, bound)
  public static int[] sample(RandomGenerator rng, int count, int origin, int bound) {
    int[] values = new int[count];
    for (int i = 0; i < count; i++) {
      values[i] = rng.nextInt(origin, bound);
    }
    return values;
  }
}
