package src.level2;

import src.level1.NotificationSender;

public class LoggingSenderDecorator implements NotificationSender {
    private final NotificationSender wrappee;

    public LoggingSenderDecorator(NotificationSender wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public void send(String message) {
        System.out.println("LOG: Start sending");
        wrappee.send(message);
        System.out.println("LOG: Done");
    }

    @Override
    public void send(String message, String recipient) {
        System.out.println("LOG: Start sending to " + recipient);
        wrappee.send(message, recipient);
        System.out.println("LOG: Done");
    }
}