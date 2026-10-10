package src.level5;

public class SubscriptionPayment implements PaymentPolicy {
    @Override
    public void charge() {
        System.out.println("Charging subscription fee");
    }
}