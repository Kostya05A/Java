package task3;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SessionRegistry {

    public record Session(String id, String user, long createdAtMillis) {}

    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    /** Атомарно: создать, только если отсутствует. Возвращает true, если создано. */
    public boolean createIfAbsent(Session s) {
        return sessions.putIfAbsent(s.id(), s) == null;
    }

    /** Эквивалент на computeIfAbsent — тоже атомарно на уровне ключа. */
    public Session createIfAbsentComputed(String id, String user, long now) {
        return sessions.computeIfAbsent(id, k -> new Session(k, user, now));
    }

    public Optional<Session> find(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public boolean remove(String id) {
        return sessions.remove(id) != null;
    }

    public int size() {
        return sessions.size();
    }
}