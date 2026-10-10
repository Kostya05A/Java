package src.level5;

public class FixedPayment implements PaymentPolicy {
    @Override
    public void charge() {
        System.out.println("Charging fixed price");
    }
}