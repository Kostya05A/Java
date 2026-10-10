package src.level2;

import src.level1.NotificationSender;

public class MetricsSenderDecorator implements NotificationSender {
    private final NotificationSender wrappee;

    public MetricsSenderDecorator(NotificationSender wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public void send(String message) {
        long start = System.currentTimeMillis();
        wrappee.send(message);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("METRICS: Time taken = " + elapsed + " ms");
    }

    @Override
    public void send(String message, String recipient) {
        long start = System.currentTimeMillis();
        wrappee.send(message, recipient);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("METRICS: Time taken = " + elapsed + " ms");
    }
}