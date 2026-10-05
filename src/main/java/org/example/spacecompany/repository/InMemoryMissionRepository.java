package org.example.spacecompany.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionStatus;

/** {@link MissionRepository} на {@link ConcurrentHashMap}. */
public class InMemoryMissionRepository implements MissionRepository {

    private final ConcurrentMap<UUID, Mission> storage = new ConcurrentHashMap<>();

    @Override
    public Mission save(Mission entity) {
        Objects.requireNonNull(entity, "entity");
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Mission> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Mission> findAll() {
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
    public List<Mission> findByStatus(MissionStatus status) {
        Objects.requireNonNull(status, "status");
        return storage.values().stream()
                .filter(m -> m.getStatus() == status)
                .toList();
    }
}
