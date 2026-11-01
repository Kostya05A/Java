package task12;
import java.util.*;

public final class InMemoryRepository<ID, T> implements Repository<ID, T> {
    private final Map<ID, T> store = new LinkedHashMap<>();

    @Override
    public void save(ID id, T entity) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(entity, "entity");
        store.put(id, entity); // повторный save — перезапись (upsert)
    }

    @Override
    public T findById(ID id) {
        Objects.requireNonNull(id);
        T t = store.get(id);
        if (t == null) throw new NoSuchElementException("id=" + id);
        return t;
    }

    @Override
    public List<T> findAll() {
        // не отдаём внутреннюю коллекцию — снимок, защищённый от мутаций
        return List.copyOf(store.values());
    }

    @Override
    public boolean deleteById(ID id) {
        Objects.requireNonNull(id);
        return store.remove(id) != null;
    }
}
