package org.example.spacecompany.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.exception.RocketNotReadyException;
import org.example.spacecompany.repository.RocketRepository;
import org.example.spacecompany.util.GameConfig;

/**
 * Бизнес-логика ракет: покупка, заправка, ремонт, поиск.
 *
 * <p>Консоль никогда не лезет в склад напрямую: она вызывает эти методы,
 * которые следят за правилами (проверка баланса, уничтоженные не летают...)
 * и двигают деньги через {@link FinanceService}.
 */
public class RocketService {

    private final RocketRepository rockets;
    private final FinanceService finance;

    public RocketService(RocketRepository rockets, FinanceService finance) {
        this.rockets = Objects.requireNonNull(rockets, "rockets");
        this.finance = Objects.requireNonNull(finance, "finance");
    }

    /**
     * Buys a new rocket of the given model.
     *
     * @throws org.example.spacecompany.exception.InsufficientFundsException
     *         when the balance is too low
     */
    public Rocket buyRocket(RocketModel model, String customName) {
        Objects.requireNonNull(model, "model");
        finance.withdraw(model.getPrice(), "Покупка ракеты " + model.getDisplayName());
        String name = (customName == null || customName.isBlank())
                ? model.getDisplayName() + " #" + (rockets.count() + 1)
                : customName.strip();
        Rocket rocket = new Rocket(name, model);
        rockets.save(rocket);
        return rocket;
    }

    /** Fills the tank; charges per missing fuel unit. */
    public void refuelRocket(UUID rocketId) {
        Rocket rocket = requireRocket(rocketId);
        if (rocket.getStatus() == RocketStatus.DESTROYED) {
            throw new RocketNotReadyException("Нельзя заправить уничтоженную ракету: " + rocket.getName());
        }
        int missing = rocket.getMaxFuel() - rocket.getFuel();
        if (missing <= 0) {
            return;
        }
        BigDecimal cost = GameConfig.FUEL_UNIT_PRICE.multiply(BigDecimal.valueOf(missing));
        finance.withdraw(cost, "Заправка " + rocket.getName() + " (+" + missing + " топлива)");
        rocket.refuel(missing);
    }

    /** Repairs a damaged rocket to 100% condition. */
    public void repairRocket(UUID rocketId) {
        Rocket rocket = requireRocket(rocketId);
        if (rocket.getStatus() == RocketStatus.DESTROYED) {
            throw new RocketNotReadyException("Уничтоженную ракету не чинят: " + rocket.getName());
        }
        int missing = 100 - rocket.getCondition();
        if (missing <= 0 && rocket.getStatus() != RocketStatus.DAMAGED) {
            return;
        }
        BigDecimal cost = GameConfig.REPAIR_PRICE_PER_POINT.multiply(BigDecimal.valueOf(missing));
        finance.withdraw(cost, "Ремонт " + rocket.getName());
        if (rocket.getStatus() == RocketStatus.IN_MAINTENANCE) {
            rocket.repair();
            rocket.finishMaintenance();
        } else {
            rocket.repair();
        }
    }

    public void sendToMaintenance(UUID rocketId) {
        requireRocket(rocketId).startMaintenance();
    }

    public void finishMaintenance(UUID rocketId) {
        Rocket rocket = requireRocket(rocketId);
        finance.withdraw(GameConfig.MAINTENANCE_FEE, "Техобслуживание: " + rocket.getName());
        rocket.finishMaintenance();
    }

    public Optional<Rocket> findById(UUID id) {
        return rockets.findById(id);
    }

    public List<Rocket> findAll() {
        return rockets.findAll();
    }

    /** Rockets ready to launch right now. Stream + method reference example. */
    public List<Rocket> findReadyRockets() {
        return rockets.findAll().stream()
                .filter(Rocket::isReadyForLaunch)
                .toList();
    }

    /** All rockets sorted by effective reliability, best first. */
    public List<Rocket> sortByReliabilityDesc() {
        return rockets.findAll().stream()
                .sorted(Comparator.comparingDouble(Rocket::getReliability).reversed())
                .toList();
    }

    /** The most expensive rocket owned (by model price). */
    public Optional<Rocket> findMostExpensive() {
        return rockets.findAll().stream()
                .max(Comparator.comparing(r -> r.getModel().getPrice()));
    }

    private Rocket requireRocket(UUID rocketId) {
        return rockets.findById(rocketId)
                .orElseThrow(() -> new RocketNotReadyException("Ракета не найдена: " + rocketId));
    }
}
