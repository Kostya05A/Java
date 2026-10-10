package level2;

public class Demo5 {
    public static void main(String[] args) {
        System.out.println("Успешное выполнение");
        try (TestResource res = new TestResource("Успех")) {
            res.use();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nОшибка в работе");
        try (TestResource res = new TestResource("Ошибка работы")) {
            res.use();
            throw new RuntimeException("Сбой в использовании");
        } catch (Exception e) {
            System.out.println("Перехвачено: " + e.getMessage());
        }

        System.out.println("\nОшибка в работе и в close");
        try (TestResource res = new TestResource("Двойной сбой")) {
            res.use();
            throw new RuntimeException("Сбой в использовании");
        } catch (Exception e) {
            System.out.println("Основное: " + e.getMessage());
            for (Throwable s : e.getSuppressed()) {
                System.out.println("Подавленное: " + s.getMessage());
            }
        }
    }
}

class TestResource implements AutoCloseable {
    private final String name;
    TestResource(String name) { 
        this.name = name; 
        System.out.println("Open: " + name); 
    }
    void use() { 
        System.out.println("Use: " + name); 
        if (name.contains("Ошибка")) throw new RuntimeException("Ошибка внутри use"); 
    }
    @Override
    public void close() throws Exception { 
        System.out.println("Close: " + name); 
        if (name.contains("Двойной")) throw new Exception("Ошибка внутри close"); 
    }
}
