package src.level5;

public class FreePayment implements PaymentPolicy {
    @Override
    public void charge() {
        System.out.println("Free course - no charge");
    }
}