package src.level3;

public interface NotifierB {
    default void notify(String message) {
        System.out.println("NotifierB: " + message);
    }
}