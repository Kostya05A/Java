package level2;

public class Demo4 {
    public static void main(String[] args) {
        System.out.println("Тест 1: " + testNormal());
        System.out.println("Тест 2: " + testHidden());
    }

    static int testNormal() {
        try {
            return 1;
        } catch (Exception e) {
            return 2;
        } finally {
            System.out.println("Finally выполнен");
        }
    }

    static int testHidden() {
        try {
            throw new RuntimeException("Ошибка в try");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка в catch");
        } finally {
            return 99;
        }
    }
}
