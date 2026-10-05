package org.example.spacecompany;

import java.math.BigDecimal;
import java.util.Random;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.repository.EmployeeRepository;
import org.example.spacecompany.repository.InMemoryEmployeeRepository;
import org.example.spacecompany.repository.InMemoryMissionRepository;
import org.example.spacecompany.repository.InMemoryRocketRepository;
import org.example.spacecompany.repository.MissionRepository;
import org.example.spacecompany.repository.RocketRepository;
import org.example.spacecompany.service.EmployeeService;
import org.example.spacecompany.service.EventService;
import org.example.spacecompany.service.FinanceService;
import org.example.spacecompany.service.MissionService;
import org.example.spacecompany.service.RocketService;
import org.example.spacecompany.service.StatisticsService;

/**
 * Shared builders for tests: a fully wired in-memory world with plenty of
 * money, so tests focus on behaviour instead of setup.
 */
public final class TestFixtures {

    private TestFixtures() {
    }

    /** Everything a test needs, already wired together. */
    public static final class World {
        public final Company company;
        public final RocketRepository rockets;
        public final EmployeeRepository employees;
        public final MissionRepository missions;
        public final FinanceService finance;
        public final EventService events;
        public final RocketService rocketService;
        public final EmployeeService employeeService;
        public final MissionService missionService;
        public final StatisticsService statistics;

        private World(Company company, RocketRepository rockets, EmployeeRepository employees,
                      MissionRepository missions, FinanceService finance, EventService events) {
            this.company = company;
            this.rockets = rockets;
            this.employees = employees;
            this.missions = missions;
            this.finance = finance;
            this.events = events;
            this.rocketService = new RocketService(rockets, finance);
            this.employeeService = new EmployeeService(employees, finance);
            this.missionService = new MissionService(missions, rockets, employees,
                    finance, events, company);
            this.statistics = new StatisticsService(company, finance);
        }

        /** Stops the background pool (call in @AfterEach). */
        public void shutdown() {
            missionService.shutdown();
        }
    }

    /** World with a deterministic, event-free flight model. */
    public static World newWorld() {
        return newWorld(new BigDecimal("100000"), true);
    }

    /** World with the given starting balance. */
    public static World newWorld(BigDecimal balance) {
        return newWorld(balance, true);
    }

    /**
     * @param quietEvents true strips random events (deterministic flights);
     *                    false keeps the default pool with a fixed seed.
     */
    public static World newWorld(BigDecimal balance, boolean quietEvents) {
        RocketRepository rockets = new InMemoryRocketRepository();
        EmployeeRepository employees = new InMemoryEmployeeRepository();
        MissionRepository missions = new InMemoryMissionRepository();
        Company company = new Company("Test Corp", balance, rockets, employees, missions);
        FinanceService finance = new FinanceService(company);
        EventService events = new EventService(new Random(42));
        if (quietEvents) {
            events.clearEvents();
        }
        return new World(company, rockets, employees, missions, finance, events);
    }

    /**
     * Finds a seed whose first {@code nextDouble()} is below {@code probability}
     * (a successful roll) or above it (a failed roll). Makes flight tests
     * deterministic without depending on magic seed constants.
     */
    public static long seedFor(boolean success, double probability) {
        for (long seed = 1; seed < 100_000; seed++) {
            boolean hit = new Random(seed).nextDouble() < probability;
            if (hit == success) {
                return seed;
            }
        }
        throw new IllegalStateException("No seed found for p=" + probability);
    }
}
