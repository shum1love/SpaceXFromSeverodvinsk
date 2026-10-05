package org.example.spacecompany.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;

/** {@link EmployeeRepository} на {@link ConcurrentHashMap}. */
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final ConcurrentMap<UUID, Employee> storage = new ConcurrentHashMap<>();

    @Override
    public Employee save(Employee entity) {
        Objects.requireNonNull(entity, "entity");
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Employee> findAll() {
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
    public List<Employee> findByRole(EmployeeRole role) {
        Objects.requireNonNull(role, "role");
        return storage.values().stream()
                .filter(e -> e.getRole() == role)
                .toList();
    }

    @Override
    public List<Employee> findAvailable() {
        List<Employee> result = new ArrayList<>();
        for (Employee employee : storage.values()) {
            if (employee.isAvailable()) {
                result.add(employee);
            }
        }
        return result;
    }
}
