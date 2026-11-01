package task14;
import java.util.*;

public final class LruCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity");
        this.capacity = capacity;
        // accessOrder=true => при get/put элемент уходит в конец (самый свежий)
        this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.capacity;
            }
        };
    }

    public V get(K key) {
        Objects.requireNonNull(key);
        return map.get(key); // null = miss (политика явная: null-значения не храним)
    }

    public void put(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        map.put(key, value);
    }

    public int size() { return map.size(); }
}
