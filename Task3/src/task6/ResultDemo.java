package task6;

public class ResultDemo {
    public static void main(String[] args) {
        Result<Integer> r1 = Result.success(42);
        Result<String>  r2 = Result.failure("boom");
        
        // r1 = r2; // Compile error: incompatible types
        
        System.out.println("r1 isOk: " + r1.isOk() + ", value: " + r1.getValue());
        System.out.println("r2 isOk: " + r2.isOk() + ", error: " + r2.getError());
        
        Result<String> mapped = r1.map(Object::toString);
        System.out.println("mapped: " + mapped.getValue());
    }
}