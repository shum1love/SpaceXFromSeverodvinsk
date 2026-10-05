package org.example.spacecompany.domain.mission;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Контракт, который выполняет компания: именованное задание вида {@link MissionType}
 * с назначенной ракетой и экипажем.
 *
 * <p>Миссия хранит ссылки (id ракеты + id экипажа), а не сами объекты —
 * так сохранение в файл остаётся тривиальным, а устаревшие копии не портят склады.
 */
public class Mission {

    private final UUID id;
    private String name;
    private final MissionType type;
    private int requiredPayloadKg;
    private BigDecimal reward;
    private MissionStatus status;
    private UUID assignedRocketId;
    private final Set<UUID> crewIds = new LinkedHashSet<>();
    /** Last computed success probability, 0..1. */
    private double successProbability;
    private final LocalDateTime createdAt;
    private LocalDateTime launchedAt;
    private LocalDateTime completedAt;
    private String resultLog;

    /** Creates a mission with defaults taken from the type. */
    public Mission(String name, MissionType type) {
        this(UUID.randomUUID(), name, type, type.getRequiredPayloadKg(),
                type.getBaseReward(), LocalDateTime.now());
    }

    /** Creates a mission with a custom payload demand and reward. */
    public Mission(String name, MissionType type, int requiredPayloadKg, BigDecimal reward) {
        this(UUID.randomUUID(), name, type, requiredPayloadKg, reward, LocalDateTime.now());
    }

    /** Full constructor used by persistence when restoring exact state. */
    public Mission(UUID id, String name, MissionType type, int requiredPayloadKg,
                   BigDecimal reward, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        setName(name);
        this.type = Objects.requireNonNull(type, "type");
        setRequiredPayloadKg(requiredPayloadKg);
        setReward(reward);
        this.status = MissionStatus.PLANNED;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название миссии не должно быть пустым");
        }
        this.name = name.strip();
    }

    public MissionType getType() {
        return type;
    }

    public int getRequiredPayloadKg() {
        return requiredPayloadKg;
    }

    public final void setRequiredPayloadKg(int requiredPayloadKg) {
        if (requiredPayloadKg < 0) {
            throw new IllegalArgumentException("Груз не может быть отрицательным");
        }
        this.requiredPayloadKg = requiredPayloadKg;
    }

    public BigDecimal getReward() {
        return reward;
    }

    public final void setReward(BigDecimal reward) {
        if (reward == null || reward.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Награда не может быть отрицательной");
        }
        this.reward = reward;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public UUID getAssignedRocketId() {
        return assignedRocketId;
    }

    public void assignRocket(UUID rocketId) {
        this.assignedRocketId = Objects.requireNonNull(rocketId, "rocketId");
    }

    public void unassignRocket() {
        this.assignedRocketId = null;
    }

    /** Unmodifiable view of assigned crew ids (insertion order preserved). */
    public Set<UUID> getCrewIds() {
        return Collections.unmodifiableSet(crewIds);
    }

    public void assignCrewMember(UUID employeeId) {
        crewIds.add(Objects.requireNonNull(employeeId, "employeeId"));
    }

    public boolean removeCrewMember(UUID employeeId) {
        return crewIds.remove(employeeId);
    }

    public void clearCrew() {
        crewIds.clear();
    }

    /** Restore hook for persistence: replaces crew membership. */
    public void restoreCrew(Set<UUID> ids) {
        crewIds.clear();
        crewIds.addAll(ids);
    }

    public int crewSize() {
        return crewIds.size();
    }

    public double getSuccessProbability() {
        return successProbability;
    }

    public void setSuccessProbability(double successProbability) {
        if (successProbability < 0.0 || successProbability > 1.0) {
            throw new IllegalArgumentException("Вероятность должна быть в пределах 0..1");
        }
        this.successProbability = successProbability;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLaunchedAt() {
        return launchedAt;
    }

    public void setLaunchedAt(LocalDateTime launchedAt) {
        this.launchedAt = launchedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getResultLog() {
        return resultLog;
    }

    public void setResultLog(String resultLog) {
        this.resultLog = resultLog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mission other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Миссия{id=%s, название='%s', тип=%s, груз=%d кг, награда=%s, статус=%s, ракета=%s, экипаж=%d, шанс=%.2f}"
                .formatted(id.toString().substring(0, 8), name, type.getDisplayName(),
                        requiredPayloadKg, reward, status,
                        assignedRocketId == null ? "-" : assignedRocketId.toString().substring(0, 8),
                        crewIds.size(), successProbability);
    }
}
