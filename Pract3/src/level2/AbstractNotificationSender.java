package src.level2;

import src.level1.NotificationSender;

public abstract class AbstractNotificationSender implements NotificationSender {
    @Override
    public final void send(String message, String recipient) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient cannot be empty");
        }
        doSend(message, recipient);
    }

    @Override
    public void send(String message) {
        doSend(message, "default");
    }

    protected abstract void doSend(String message, String recipient);
}