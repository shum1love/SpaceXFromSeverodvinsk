package org.example.spacecompany.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketStatus;

/**
 * {@link RocketRepository} на {@link ConcurrentHashMap}.
 *
 * <p>Конкурентная мапа (а не обычный {@code HashMap}) — потому что миссии
 * могут запускаться из нескольких потоков и читать состояние ракет
 * одновременно. Дополнительных {@code synchronized} для самой мапы не нужно;
 * составные действия («проверил-запустил») охраняются в {@code MissionService}.
 */
public class InMemoryRocketRepository implements RocketRepository {

    private final ConcurrentMap<UUID, Rocket> storage = new ConcurrentHashMap<>();

    @Override
    public Rocket save(Rocket entity) {
        Objects.requireNonNull(entity, "entity");
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Rocket> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Rocket> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        if (id == null) {
            return false;
        }
        return storage.remove(id) != null;
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            return false;
        }
        return storage.containsKey(id);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public List<Rocket> findByStatus(RocketStatus status) {
        Objects.requireNonNull(status, "status");
        List<Rocket> result = new ArrayList<>();
        for (Rocket rocket : storage.values()) {
            if (rocket.getStatus() == status) {
                result.add(rocket);
            }
        }
        return result;
    }

    @Override
    public List<Rocket> findByNameContaining(String text) {
        if (text == null || text.isBlank()) {
            return findAll();
        }
        String needle = text.toLowerCase(Locale.ROOT);
        return storage.values().stream()
                .filter(r -> r.getName().toLowerCase(Locale.ROOT).contains(needle))
                .toList();
    }
}
