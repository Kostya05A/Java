package src.level4;

public class Level4Demo {
    public static void main(String[] args) {
        System.out.println("Задание 11. Равенство в иерархии");
        demonstrateEquality();
        System.out.println();

        System.out.println("Задание 12. Sealed-иерархия результата");
        demonstrateSealedHierarchy();
    }

    private static void demonstrateEquality() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        ColoredPoint cp1 = new ColoredPoint(1, 2, "red");

        System.out.println("p1.equals(p2): " + p1.equals(p2));
        System.out.println("p1.equals(cp1): " + p1.equals(cp1));
        System.out.println("cp1.equals(p1): " + cp1.equals(p1));
    }

    private static void demonstrateSealedHierarchy() {
        EnrollmentResult result1 = new Accepted("Welcome!");
        EnrollmentResult result2 = new Rejected("Course full");
        EnrollmentResult result3 = new WaitListed(5);

        handleResult(result1);
        handleResult(result2);
        handleResult(result3);
    }

    private static void handleResult(EnrollmentResult result) {
        switch (result) {
            case Accepted a -> System.out.println("Accepted: " + a.message());
            case Rejected r -> System.out.println("Rejected: " + r.reason());
            case WaitListed w -> System.out.println("WaitListed at position: " + w.position());
        }
    }
}