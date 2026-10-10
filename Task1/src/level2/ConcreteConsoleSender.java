package src.level2;

public class ConcreteConsoleSender extends AbstractNotificationSender {
    @Override
    protected void doSend(String message, String recipient) {
        System.out.println("Console output to " + recipient + ": " + message);
    }
}