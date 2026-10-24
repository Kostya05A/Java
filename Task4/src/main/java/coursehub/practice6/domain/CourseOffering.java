package coursehub.practice6.domain;

// то, что продаём: расписание и полная цена в одной валюте
public record CourseOffering(WebinarSeriesPlan plan, Money fullPrice) {
  public CourseOffering {
    if (plan == null || fullPrice == null) {
      throw new IllegalArgumentException("plan and fullPrice must not be null");
    }
  }
}
