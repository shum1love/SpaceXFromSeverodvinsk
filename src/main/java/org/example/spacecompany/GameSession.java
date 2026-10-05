package org.example.spacecompany;

import java.math.BigDecimal;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.Pilot;
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
import org.example.spacecompany.service.SaveLoadService;
import org.example.spacecompany.service.StatisticsService;
import org.example.spacecompany.util.GameConfig;

/**
 * Общее ядро игры: репозитории, компания и все сервисы, собранные вместе.
 *
 * <p>Архитектура «одно ядро — несколько интерфейсов»: и консольное меню
 * ({@code console}), и JavaFX-интерфейс ({@code ui}) работают поверх ОДНОГО
 * {@code GameSession} и одних и тех же сервисов. Никакой бизнес-логики здесь
 * нет — только сборка (ручное внедрение зависимостей) и стартовый набор.
 *
 * <p>Класс не делает никакого ввода/вывода: сообщения печатает тот интерфейс,
 * который сессию использует. Поэтому сессию можно создавать и в тестах.
 */
public class GameSession {

    private final RocketRepository rockets = new InMemoryRocketRepository();
    private final EmployeeRepository employees = new InMemoryEmployeeRepository();
    private final MissionRepository missions = new InMemoryMissionRepository();
    private final Company company;
    private final FinanceService finance;
    private final RocketService rocketService;
    private final EmployeeService employeeService;
    private final EventService events;
    private final MissionService missionService;
    private final StatisticsService statistics;
    private final SaveLoadService saveLoad = new SaveLoadService();

    /** Создаёт новую игру: компания + стартовый капитал + подарок города. */
    public GameSession(String companyName) {
        String name = (companyName == null || companyName.isBlank())
                ? "Северодвинские Звёзды" : companyName.strip();
        this.company = new Company(name, BigDecimal.ZERO, rockets, employees, missions);
        this.finance = new FinanceService(company);
        finance.deposit(GameConfig.STARTING_BALANCE, "Стартовые инвестиции");
        this.rocketService = new RocketService(rockets, finance);
        this.employeeService = new EmployeeService(employees, finance);
        this.events = new EventService();
        this.missionService = new MissionService(missions, rockets, employees,
                finance, events, company);
        this.statistics = new StatisticsService(company, finance);
        seedStarterKit();
    }

    /**
     * Стартовый набор — подарок города: заправленная Falcon 9 и два специалиста.
     * Денег не движется (подарок, а не покупка). Без печати: интерфейс сам решает,
     * что и как сообщить игроку.
     */
    private void seedStarterKit() {
        Rocket gift = new Rocket("Северодвинск-1", RocketModel.FALCON_9);
        gift.refuel();
        rockets.save(gift);
        employees.save(new Pilot("Анна Соколова", new BigDecimal("1200"), 4, 6));
        employees.save(new Engineer("Дмитрий Волков", new BigDecimal("1100"), 5, 7));
    }

    public Company company() {
        return company;
    }

    public FinanceService finance() {
        return finance;
    }

    public RocketService rockets() {
        return rocketService;
    }

    public EmployeeService employees() {
        return employeeService;
    }

    public EventService events() {
        return events;
    }

    public MissionService missions() {
        return missionService;
    }

    public StatisticsService statistics() {
        return statistics;
    }

    public SaveLoadService saveLoad() {
        return saveLoad;
    }

    /** Останавливает фоновый пул полётов. Вызвать один раз при выходе. */
    public void shutdown() {
        missionService.shutdown();
    }
}
