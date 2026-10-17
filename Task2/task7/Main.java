public class Main {
  public static void main(String[] args) {
    // Optional
    for (int i : new int[]{1, 99}) {
      FindCourse.find(i).ifPresentOrElse(
          c -> System.out.println("found id=" + c.id()),
          () -> System.out.println("not found"));

    // checked exception
      try {
        Enroll.enroll(100, i);
        System.out.println("enrolled student=100 course=" + i);
      } catch (EnrollmentRejectedException e) {
        System.out.println("rejected: " + e.reasonCode());
      }
    }
  }
}
