package src.level1;

public class EmailSender implements NotificationSender {
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }

    @Override
    public void send(String message, String recipient) {
        System.out.println("Email to " + recipient + ": " + message);
    }
}