package task14;

public class LruCacheDemo {
    public static void main(String[] args) {
        LruCache<String, Integer> cache = new LruCache<>(3);
        
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        System.out.println("После добавления a,b,c: " + cache.size());
        
        cache.get("a"); // "a" теперь самый свежий
        cache.put("d", 4); // вытесняется "b"
        
        System.out.println("После get(a) и put(d): " + cache.size());
        System.out.println("a=" + cache.get("a"));
        System.out.println("b=" + cache.get("b")); // null, вытеснен
        System.out.println("c=" + cache.get("c"));
        System.out.println("d=" + cache.get("d"));
        
        try {
            cache.put(null, 5);
        } catch (NullPointerException e) {
            System.out.println("Null ключ защищён");
        }
    }
}