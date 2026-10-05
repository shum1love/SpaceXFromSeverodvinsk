package org.example.spacecompany.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Физическая ракета, принадлежащая компании.
 *
 * <p>Технические пределы (цена, бак, груз, стоимость запуска, базовая
 * надёжность) берутся из неизменяемой {@link RocketModel}; изменяемое лётное
 * состояние (топливо, корпус, статус, счётчики) живёт здесь.
 *
 * <p>Равенство — только по {@code id}: два объекта {@code Rocket} с одним id —
 * одна и та же физическая ракета, даже если одна копия устарела.
 */
public class Rocket {

    /** Condition below which the rocket counts as damaged. */
    public static final int DAMAGED_BELOW_CONDITION = 50;
    /** Condition at or below which a failed mission destroys the rocket. */
    public static final int DESTROYED_AT_CONDITION = 0;

    private final UUID id;
    private String name;
    private final RocketModel model;
    private int fuel;
    /** Hull/engines health, 0..100. */
    private int condition;
    private RocketStatus status;
    private int launchCount;
    private int successCount;

    /** Creates a brand-new rocket of the given model with a generated id. */
    public Rocket(String name, RocketModel model) {
        this(UUID.randomUUID(), name, model);
    }

    /** Creates a rocket with an explicit id (used by save/load and tests). */
    public Rocket(UUID id, String name, RocketModel model) {
        this.id = Objects.requireNonNull(id, "id");
        this.model = Objects.requireNonNull(model, "model");
        setName(name);
        this.fuel = 0;
        this.condition = 100;
        this.status = RocketStatus.NO_FUEL;
        this.launchCount = 0;
        this.successCount = 0;
    }

    /** Full constructor used by persistence when restoring exact state. */
    public Rocket(UUID id, String name, RocketModel model, int fuel, int condition,
                  RocketStatus status, int launchCount, int successCount) {
        this.id = Objects.requireNonNull(id, "id");
        this.model = Objects.requireNonNull(model, "model");
        setName(name);
        setFuel(fuel);
        setCondition(condition);
        this.status = Objects.requireNonNull(status, "status");
        if (launchCount < 0 || successCount < 0 || successCount > launchCount) {
            throw new IllegalArgumentException(
                    "Некорректные счётчики: запусков=" + launchCount + ", успешных=" + successCount);
        }
        this.launchCount = launchCount;
        this.successCount = successCount;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название ракеты не должно быть пустым");
        }
        this.name = name.strip();
    }

    public RocketModel getModel() {
        return model;
    }

    public int getFuel() {
        return fuel;
    }

    public int getMaxFuel() {
        return model.getMaxFuel();
    }

    public int getCondition() {
        return condition;
    }

    public RocketStatus getStatus() {
        return status;
    }

    public int getLaunchCount() {
        return launchCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getPayloadCapacityKg() {
        return model.getPayloadCapacityKg();
    }

    /**
     * Effective reliability in range [0, 1]: base reliability of the model
     * scaled down by current condition. A rocket at 100% condition flies at
     * full base reliability; at 0% it flies at half of it.
     */
    public double getReliability() {
        return model.getBaseReliability() * (0.5 + condition / 200.0);
    }

    /** Success rate of past launches, 0.0 when the rocket never flew. */
    public double getSuccessRate() {
        if (launchCount == 0) {
            return 0.0;
        }
        return (double) successCount / launchCount;
    }

    private void setFuel(int fuel) {
        if (fuel < 0 || fuel > model.getMaxFuel()) {
            throw new IllegalArgumentException(
                    "Топливо должно быть в пределах 0.." + model.getMaxFuel() + ", получено " + fuel);
        }
        this.fuel = fuel;
    }

    private void setCondition(int condition) {
        if (condition < 0 || condition > 100) {
            throw new IllegalArgumentException("Состояние должно быть в пределах 0..100, получено " + condition);
        }
        this.condition = condition;
    }

    /** Fills the tank completely. Overloaded: see {@link #refuel(int)}. */
    public void refuel() {
        refuel(getMaxFuel() - fuel);
    }

    /**
     * Adds the given amount of fuel. Overloaded: see {@link #refuel()}.
     *
     * @param amount fuel units to add, must be positive
     */
    public void refuel(int amount) {
        if (status == RocketStatus.DESTROYED) {
            throw new IllegalStateException("Нельзя заправить уничтоженную ракету: " + name);
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Долить надо положительное количество, получено " + amount);
        }
        setFuel(Math.min(getMaxFuel(), fuel + amount));
        refreshReadiness();
    }

    /** Burns fuel, e.g. during a launch. */
    public void consumeFuel(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным, получено " + amount);
        }
        setFuel(Math.max(0, fuel - amount));
        refreshReadiness();
    }

    /** Applies damage (0..100 points). May change status to DAMAGED/DESTROYED. */
    public void damage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Урон не может быть отрицательным");
        }
        if (status == RocketStatus.DESTROYED) {
            return;
        }
        setCondition(Math.max(0, condition - amount));
        if (condition <= DESTROYED_AT_CONDITION) {
            status = RocketStatus.DESTROYED;
        } else if (condition < DAMAGED_BELOW_CONDITION) {
            if (status != RocketStatus.ON_MISSION && status != RocketStatus.IN_MAINTENANCE) {
                status = RocketStatus.DAMAGED;
            }
        }
    }

    /**
     * Restores condition to 100%. Only meaningful while in maintenance or
     * damaged; a destroyed rocket can never be repaired.
     */
    public void repair() {
        if (status == RocketStatus.DESTROYED) {
            throw new IllegalStateException("Уничтоженную ракету не чинят: " + name);
        }
        setCondition(100);
        refreshReadiness();
    }

    public void startMaintenance() {
        if (status == RocketStatus.DESTROYED) {
            throw new IllegalStateException("Уничтоженную ракету не обслуживают: " + name);
        }
        if (status == RocketStatus.ON_MISSION) {
            throw new IllegalStateException("Ракета на миссии, обслуживать нельзя: " + name);
        }
        status = RocketStatus.IN_MAINTENANCE;
    }

    public void finishMaintenance() {
        if (status != RocketStatus.IN_MAINTENANCE) {
            throw new IllegalStateException("Ракета не на обслуживании: " + name);
        }
        refreshReadiness();
    }

    public void markOnMission() {
        status = RocketStatus.ON_MISSION;
    }

    /**
     * Transitions a rocket that finished a flight back to ground handling:
     * recomputes READY / NO_FUEL / DAMAGED from fuel + condition.
     * Does nothing unless the rocket {@link #markOnMission was marked} as flying.
     */
    public void returnFromMission() {
        if (status == RocketStatus.ON_MISSION) {
            status = RocketStatus.NO_FUEL;
            refreshReadiness();
        }
    }

    public void markDestroyed() {
        setCondition(0);
        status = RocketStatus.DESTROYED;
    }

    /** Records one finished launch and wears the rocket down a little. */
    public void recordLaunch(boolean success) {
        launchCount++;
        if (success) {
            successCount++;
            setCondition(Math.max(0, condition - 5));
        } else {
            setCondition(Math.max(0, condition - 30));
        }
        if (condition <= DESTROYED_AT_CONDITION) {
            status = RocketStatus.DESTROYED;
        }
    }

    /** Recomputes READY / NO_FUEL / DAMAGED from fuel + condition. */
    public void refreshReadiness() {
        if (status == RocketStatus.DESTROYED
                || status == RocketStatus.ON_MISSION
                || status == RocketStatus.IN_MAINTENANCE) {
            return;
        }
        if (condition < DAMAGED_BELOW_CONDITION) {
            status = RocketStatus.DAMAGED;
        } else if (fuel < getMaxFuel()) {
            status = RocketStatus.NO_FUEL;
        } else {
            status = RocketStatus.READY;
        }
    }

    /** True only for a fueled, healthy rocket standing by on the pad. */
    public boolean isReadyForLaunch() {
        return status == RocketStatus.READY && fuel >= getMaxFuel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rocket other)) {
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
        return "Ракета{id=%s, название='%s', модель=%s, топливо=%d/%d, состояние=%d%%, статус=%s, запусков=%d, успешных=%d}"
                .formatted(id.toString().substring(0, 8), name, model.getDisplayName(),
                        fuel, getMaxFuel(), condition, status, launchCount, successCount);
    }
}
