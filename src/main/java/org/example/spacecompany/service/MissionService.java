package org.example.spacecompany.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.domain.LaunchRecord;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeStatus;
import org.example.spacecompany.domain.event.EventContext;
import org.example.spacecompany.domain.event.GameEvent;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionResult;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.exception.MissionValidationException;
import org.example.spacecompany.repository.EmployeeRepository;
import org.example.spacecompany.repository.MissionRepository;
import org.example.spacecompany.repository.RocketRepository;
import org.example.spacecompany.util.GameConfig;

/**
 * Жизнь миссии: создать → назначить ракету/экипаж → проверить → запустить.
 *
 * <p>Запуск в двух видах: блокирующий {@link #launch(UUID)} и фоновый
 * {@link #launchAsync(UUID)} / {@link #launchAllReady()}. Фоновый показывает
 * настоящую многопоточность ({@link ExecutorService},
 * {@link Callable}, {@link Future}, {@link CompletableFuture}), а ядро полёта
 * остаётся однопоточным и легко тестируется.
 *
 * <p>Потокобезопасность: склады — на конкурентных мапах; двойной запуск
 * <i>одной</i> миссии запрещён синхронизацией на объекте миссии. Общий
 * {@link AtomicInteger} считает завершённые полёты без замков.
 */
public class MissionService {

    private final MissionRepository missions;
    private final RocketRepository rockets;
    private final EmployeeRepository employees;
    private final FinanceService finance;
    private final EventService events;
    private final Company company;
    private final ExecutorService executor;
    private final AtomicInteger completedFlights = new AtomicInteger();

    public MissionService(MissionRepository missions,
                          RocketRepository rockets,
                          EmployeeRepository employees,
                          FinanceService finance,
                          EventService events,
                          Company company) {
        this.missions = Objects.requireNonNull(missions, "missions");
        this.rockets = Objects.requireNonNull(rockets, "rockets");
        this.employees = Objects.requireNonNull(employees, "employees");
        this.finance = Objects.requireNonNull(finance, "finance");
        this.events = Objects.requireNonNull(events, "events");
        this.company = Objects.requireNonNull(company, "company");
        this.executor = Executors.newFixedThreadPool(4);
    }

    // ------------------------------------------------------------------
    // Setup: create / assign / cancel
    // ------------------------------------------------------------------

    /** Creates a mission with defaults taken from the type. */
    public Mission createMission(String name, MissionType type) {
        Mission mission = new Mission(name, type);
        missions.save(mission);
        return mission;
    }

    /** Creates a mission with a custom payload demand and reward. */
    public Mission createMission(String name, MissionType type,
                                 int requiredPayloadKg, BigDecimal reward) {
        Mission mission = new Mission(name, type, requiredPayloadKg, reward);
        missions.save(mission);
        return mission;
    }

    public void assignRocket(UUID missionId, UUID rocketId) {
        Mission mission = requireMission(missionId);
        requireEditable(mission);
        Rocket rocket = rockets.findById(rocketId)
                .orElseThrow(() -> new MissionValidationException("Ракета не найдена: " + rocketId));
        if (rocket.getStatus() == RocketStatus.DESTROYED) {
            throw new MissionValidationException("Нельзя назначить уничтоженную ракету: " + rocket.getName());
        }
        mission.assignRocket(rocketId);
        refreshReadiness(missionId);
    }

    public void unassignRocket(UUID missionId) {
        Mission mission = requireMission(missionId);
        requireEditable(mission);
        mission.unassignRocket();
        mission.setStatus(MissionStatus.PLANNED);
    }

    public void assignCrewMember(UUID missionId, UUID employeeId) {
        Mission mission = requireMission(missionId);
        requireEditable(mission);
        Employee employee = employees.findById(employeeId)
                .orElseThrow(() -> new MissionValidationException("Сотрудник не найден: " + employeeId));
        if (!employee.isAvailable()) {
            throw new MissionValidationException(
                    "Сотрудник «" + employee.getName() + "» недоступен (" + employee.getStatus() + ")");
        }
        mission.assignCrewMember(employeeId);
        refreshReadiness(missionId);
    }

    public void removeCrewMember(UUID missionId, UUID employeeId) {
        Mission mission = requireMission(missionId);
        requireEditable(mission);
        mission.removeCrewMember(employeeId);
        refreshReadiness(missionId);
    }

    /** Cancels a planned mission. Finished missions cannot be cancelled. */
    public void cancelMission(UUID missionId) {
        Mission mission = requireMission(missionId);
        if (mission.getStatus().isTerminal()) {
            throw new MissionValidationException("Миссия уже завершена: " + mission.getStatus());
        }
        if (mission.getStatus() == MissionStatus.IN_PROGRESS) {
            throw new MissionValidationException("Нельзя отменить летящую миссию");
        }
        mission.setStatus(MissionStatus.CANCELLED);
    }

    /**
     * Revalidates the mission and updates PLANNED/READY accordingly.
     *
     * @return true when the mission is now READY
     */
    public boolean refreshReadiness(UUID missionId) {
        Mission mission = requireMission(missionId);
        if (mission.getStatus().isTerminal() || mission.getStatus() == MissionStatus.IN_PROGRESS) {
            return mission.getStatus() == MissionStatus.IN_PROGRESS;
        }
        try {
            MissionValidator.validateForLaunch(mission, requireAssignedRocket(mission), resolveCrew(mission));
            mission.setStatus(MissionStatus.READY);
            return true;
        } catch (MissionValidationException e) {
            if (mission.getStatus() == MissionStatus.READY) {
                mission.setStatus(MissionStatus.PLANNED);
            }
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Launch: blocking
    // ------------------------------------------------------------------

    /** Launches the mission, blocking until the flight finishes. */
    public MissionResult launch(UUID missionId) {
        return launch(missionId, new Random());
    }

    /**
     * Launches the mission with an explicit random generator.
     * Tests pass a seeded {@code Random} to make flights deterministic.
     * Overloaded: see {@link #launch(UUID)}.
     */
    public MissionResult launch(UUID missionId, Random random) {
        Objects.requireNonNull(random, "random");
        Mission mission = requireMission(missionId);
        // Guards against double-launch of the same mission from two threads.
        synchronized (mission) {
            return flyMission(mission, random);
        }
    }

    private MissionResult flyMission(Mission mission, Random random) {
        Rocket rocket = requireAssignedRocket(mission);
        List<Employee> crew = resolveCrew(mission);

        // 1. Validate BEFORE touching any money or statuses.
        MissionValidator.validateForLaunch(mission, rocket, crew);

        // 2. Pay the launch cost. Unchecked InsufficientFundsException propagates
        //    and the mission simply stays READY.
        finance.withdraw(rocket.getModel().getLaunchCost(),
                "Стоимость запуска: " + mission.getName() + " на " + rocket.getName());

        // 3. Mark everyone as busy.
        LocalDateTime launchedAt = LocalDateTime.now();
        mission.setStatus(MissionStatus.IN_PROGRESS);
        mission.setLaunchedAt(launchedAt);
        rocket.markOnMission();
        for (Employee member : crew) {
            member.setStatus(EmployeeStatus.ON_MISSION);
        }

        boolean finished = false;
        try {
            // 4. Probability + random events.
            double probability = ProbabilityCalculator.calculateSuccessProbability(mission, rocket, crew);
            EventContext context = new EventContext();
            List<GameEvent> firedEvents = events.applyRandomEvents(context);
            double finalProbability = clamp(probability + context.getProbabilityModifier());
            mission.setSuccessProbability(finalProbability);

            // 5. The roll.
            boolean success = random.nextDouble() < finalProbability;

            // 6. Burn the fuel and wear the rocket.
            rocket.consumeFuel(rocket.getFuel());
            rocket.recordLaunch(success);

            BigDecimal rewardPaid = BigDecimal.ZERO;
            BigDecimal extraCost = context.getExtraCost();

            StringBuilder log = new StringBuilder(256);
            log.append("Бортовой журнал «").append(mission.getName()).append("»:\n");
            log.append("  Ракета: ").append(rocket.getName())
                    .append(" (надёжность ").append("%.2f".formatted(rocket.getReliability())).append(")\n");
            log.append("  Экипаж: ").append(crew.size()).append(" чел.\n");
            log.append("  Шанс успеха: ").append("%.1f%%".formatted(finalProbability * 100.0)).append('\n');
            for (String entry : context.getLogEntries()) {
                log.append("  ").append(entry).append('\n');
            }

            if (success) {
                rewardPaid = mission.getReward().add(context.getBonusReward());
                finance.deposit(rewardPaid, "Награда: " + mission.getName());
                mission.setStatus(MissionStatus.SUCCESS);
                log.append("  Итог: УСПЕХ, награда ").append(rewardPaid);
            } else {
                mission.setStatus(MissionStatus.FAILED);
                log.append("  Итог: ПРОВАЛ");
                if (random.nextDouble() < GameConfig.DESTROY_ON_FAILURE_CHANCE
                        || rocket.getCondition() <= 0) {
                    rocket.markDestroyed();
                    log.append(" — ракета УНИЧТОЖЕНА");
                }
            }

            // 7. Unexpected bills (repairs, penalties) apply on top of the outcome.
            if (extraCost.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    finance.withdraw(extraCost, "Расходы от происшествий: " + mission.getName());
                } catch (RuntimeException e) {
                    // A broke company cannot dodge the bill, but the flight
                    // outcome itself must still be recorded.
                    log.append("\n  ВНИМАНИЕ: не хватило денег на расходы: ").append(e.getMessage());
                    extraCost = BigDecimal.ZERO;
                }
                if (extraCost.compareTo(BigDecimal.ZERO) > 0) {
                    log.append(" (расходы на происшествия ").append(extraCost).append(')');
                }
            }

            // 8. Everyone comes home: crew gains experience and is released,
            //    the rocket returns to ground handling.
            LocalDateTime completedAt = LocalDateTime.now();
            for (Employee member : crew) {
                member.gainExperience();
                member.setStatus(EmployeeStatus.AVAILABLE);
            }
            if (rocket.getStatus() != RocketStatus.DESTROYED) {
                rocket.returnFromMission();
            }
            mission.setCompletedAt(completedAt);
            mission.setResultLog(log.toString());
            finished = true;

            Duration flightTime = simulatedFlightTime(mission, random);
            MissionResult result = new MissionResult(mission.getId(), success,
                    success ? "Миссия «" + mission.getName() + "» успешна!"
                            : "Миссия «" + mission.getName() + "» провалена.",
                    rewardPaid, extraCost, context.getLogEntries(),
                    completedAt, flightTime);

            company.addLaunchRecord(new LaunchRecord(mission.getName(), rocket.getName(),
                    success, result.netProfit()));
            completedFlights.incrementAndGet();
            return result;
        } finally {
            if (!finished) {
                // Genuine use of finally: an unexpected error mid-flight must
                // never leave the rocket and crew stuck ON_MISSION forever.
                for (Employee member : crew) {
                    if (member.getStatus() == EmployeeStatus.ON_MISSION) {
                        member.setStatus(EmployeeStatus.AVAILABLE);
                    }
                }
                if (rocket.getStatus() == RocketStatus.ON_MISSION) {
                    rocket.returnFromMission();
                }
                mission.setStatus(MissionStatus.PLANNED);
            }
        }
    }

    // ------------------------------------------------------------------
    // Launch: background (concurrency demo)
    // ------------------------------------------------------------------

    /**
     * Launches one mission in the background pool.
     *
     * @return a {@link Future} resolving to the flight outcome
     */
    public Future<MissionResult> launchAsync(UUID missionId) {
        Callable<MissionResult> task = () -> launch(missionId);
        return executor.submit(task);
    }

    /** Background launch as a {@link CompletableFuture} (modern alternative). */
    public CompletableFuture<MissionResult> launchAsyncCompletable(UUID missionId) {
        return CompletableFuture.supplyAsync(() -> launch(missionId), executor);
    }

    /**
     * Launches every READY mission concurrently and returns one future per
     * mission. Demonstrates fan-out with an {@link ExecutorService}.
     */
    public List<Future<MissionResult>> launchAllReady() {
        List<Future<MissionResult>> futures = new ArrayList<>();
        for (Mission mission : missions.findByStatus(MissionStatus.READY)) {
            final UUID id = mission.getId();
            futures.add(executor.submit(() -> launch(id)));
        }
        return futures;
    }

    /** Flights finished through this service (thread-safe counter). */
    public int getCompletedFlightCount() {
        return completedFlights.get();
    }

    /** Stops the background pool. Call once when the application exits. */
    public void shutdown() {
        executor.shutdown();
    }

    // ------------------------------------------------------------------
    // Queries
    // ------------------------------------------------------------------

    public Optional<Mission> findById(UUID id) {
        return missions.findById(id);
    }

    public List<Mission> findAll() {
        return missions.findAll();
    }

    public List<Mission> findReady() {
        return missions.findByStatus(MissionStatus.READY);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Mission requireMission(UUID missionId) {
        return missions.findById(missionId)
                .orElseThrow(() -> new MissionValidationException("Миссия не найдена: " + missionId));
    }

    private void requireEditable(Mission mission) {
        if (mission.getStatus().isTerminal()) {
            throw new MissionValidationException(
                    "Миссия «" + mission.getName() + "» уже завершена (" + mission.getStatus() + ")");
        }
        if (mission.getStatus() == MissionStatus.IN_PROGRESS) {
            throw new MissionValidationException(
                    "Миссия «" + mission.getName() + "» уже летит");
        }
    }

    private Rocket requireAssignedRocket(Mission mission) {
        if (mission.getAssignedRocketId() == null) {
            throw new MissionValidationException(
                    "Миссии «" + mission.getName() + "» не назначена ракета");
        }
        return rockets.findById(mission.getAssignedRocketId())
                .orElseThrow(() -> new MissionValidationException("Назначенная ракета больше не существует"));
    }

    private List<Employee> resolveCrew(Mission mission) {
        List<Employee> crew = new ArrayList<>();
        for (UUID id : mission.getCrewIds()) {
            Employee member = employees.findById(id).orElse(null);
            if (member == null) {
                throw new MissionValidationException("Назначенный член экипажа больше не существует: " + id);
            }
            crew.add(member);
        }
        return crew;
    }

    private static double clamp(double value) {
        return Math.min(ProbabilityCalculator.MAX_PROBABILITY,
                Math.max(ProbabilityCalculator.MIN_PROBABILITY, value));
    }

    /** Simulated in-fiction flight time derived from mission difficulty. */
    private static Duration simulatedFlightTime(Mission mission, Random random) {
        long hours = (long) mission.getType().getDifficulty() * 8 + random.nextInt(24);
        return Duration.ofHours(hours);
    }
}
