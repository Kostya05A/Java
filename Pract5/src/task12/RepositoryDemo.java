package task12;

public class RepositoryDemo {
    public static void main(String[] args) {
        Repository<Integer, String> repo = new InMemoryRepository<>();
        
        repo.save(1, "Alice");
        repo.save(2, "Bob");
        
        System.out.println("findById(1): " + repo.findById(1));
        System.out.println("findAll: " + repo.findAll());
        
        repo.save(1, "Alice Updated");
        System.out.println("После повторного save: " + repo.findById(1));
        
        System.out.println("deleteById(2): " + repo.deleteById(2));
        System.out.println("findAll после удаления: " + repo.findAll());
        
        try {
            repo.save(null, "test");
        } catch (NullPointerException e) {
            System.out.println("Null защищён: " + e.getMessage());
        }
    }
}