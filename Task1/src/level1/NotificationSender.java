package src.level1;

public interface NotificationSender {
    void send(String message);
    void send(String message, String recipient);
}
