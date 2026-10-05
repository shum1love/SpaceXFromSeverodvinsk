package org.example.spacecompany.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Неизменяемая запись истории: что случилось с миссией и сколько денег
 * компания на ней заработала или потеряла.
 */
public final class LaunchRecord {

    private final UUID id;
    private final String missionName;
    private final String rocketName;
    private final boolean success;
    private final BigDecimal netProfit;
    private final LocalDateTime timestamp;

    public LaunchRecord(String missionName, String rocketName,
                        boolean success, BigDecimal netProfit) {
        this(UUID.randomUUID(), missionName, rocketName, success, netProfit, LocalDateTime.now());
    }

    public LaunchRecord(UUID id, String missionName, String rocketName,
                        boolean success, BigDecimal netProfit, LocalDateTime timestamp) {
        this.id = Objects.requireNonNull(id, "id");
        if (missionName == null || missionName.isBlank()) {
            throw new IllegalArgumentException("Название миссии не должно быть пустым");
        }
        if (rocketName == null || rocketName.isBlank()) {
            throw new IllegalArgumentException("Название ракеты не должно быть пустым");
        }
        this.missionName = missionName.strip();
        this.rocketName = rocketName.strip();
        this.success = success;
        this.netProfit = Objects.requireNonNull(netProfit, "netProfit");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
    }

    public UUID getId() {
        return id;
    }

    public String getMissionName() {
        return missionName;
    }

    public String getRocketName() {
        return rocketName;
    }

    public boolean isSuccess() {
        return success;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LaunchRecord other)) {
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
        return "[%s] %s на «%s»: %s (итого %s)".formatted(
                timestamp, missionName, rocketName,
                success ? "УСПЕХ" : "ПРОВАЛ", netProfit);
    }
}
