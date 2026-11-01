package task7;
import java.util.*;
import java.util.function.Function;

public class GenericMethods {

    public static <T> T firstOrThrow(List<T> list) {
        if (list == null || list.isEmpty())
            throw new NoSuchElementException("empty list");
        return list.get(0);
    }

    public static <T> void swap(List<T> list, int i, int j) {
        if (i < 0 || j < 0 || i >= list.size() || j >= list.size())
            throw new IndexOutOfBoundsException();
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }

    /** Индексирует список по ключу; при конфликте — явная ошибка. */
    public static <T, K> Map<K, T> indexBy(List<T> list, Function<? super T, ? extends K> keyFn) {
        Map<K, T> map = new LinkedHashMap<>(list.size());
        for (T t : list) {
            K k = keyFn.apply(t);
            T prev = map.putIfAbsent(k, t);
            if (prev != null) {
                throw new IllegalArgumentException(
                    "duplicate key " + k + ": " + prev + " vs " + t);
            }
        }
        return map;
    }
}