package src.level1;

public class ConsoleSender implements NotificationSender {
    @Override
    public void send(String message) {
        System.out.println("Console: " + message);
    }

    @Override
    public void send(String message, String recipient) {
        System.out.println("Console to " + recipient + ": " + message);
    }
}