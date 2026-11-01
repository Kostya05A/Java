package src.level3;

public interface NotifierA {
    default void notify(String message) {
        System.out.println("NotifierA: " + message);
    }
}