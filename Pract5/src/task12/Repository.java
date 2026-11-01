package task12;
import java.util.*;

public interface Repository<ID, T> {
    void save(ID id, T entity);          // семантика: повторный save — замена
    T findById(ID id);                   // бросает NoSuchElementException, если нет
    List<T> findAll();                   // неизменяемый снимок
    boolean deleteById(ID id);
}