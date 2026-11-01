package src.level2;

public class ConcreteEmailSender extends AbstractNotificationSender {
    @Override
    protected void doSend(String message, String recipient) {
        System.out.println("Sending email to " + recipient + ": " + message);
    }
}