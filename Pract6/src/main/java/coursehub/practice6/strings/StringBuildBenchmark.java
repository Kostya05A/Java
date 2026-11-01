package coursehub.practice6.strings;

import java.util.ArrayList;
import java.util.List;

// учебное измерение: это не JMH — без прогрева, повторов и статистики
public class StringBuildBenchmark {
  public static void main(String[] args) {
    List<String> lines = new ArrayList<>();
    for (int i = 1; i <= 10_000; i++) {
      lines.add("row " + i);
    }

    long plusStart = System.nanoTime();
    String byPlus = ReportBuilder.withPlus(lines);
    long plusMs = (System.nanoTime() - plusStart) / 1_000_000;

    long builderStart = System.nanoTime();
    String byBuilder = ReportBuilder.withBuilder(lines);
    long builderMs = (System.nanoTime() - builderStart) / 1_000_000;

    // результат обязан совпасть: разница только в способе сборки
    System.out.println("same result: " + byPlus.equals(byBuilder));
    System.out.println("plus:    " + plusMs + " ms");
    System.out.println("builder: " + builderMs + " ms");
  }
}
