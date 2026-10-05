package org.example.spacecompany.console;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.exception.GameSaveException;
import org.example.spacecompany.exception.SpaceCompanyException;
import org.example.spacecompany.service.EmployeeService;
import org.example.spacecompany.service.FinanceService;
import org.example.spacecompany.service.MissionService;
import org.example.spacecompany.service.RocketService;
import org.example.spacecompany.service.SaveLoadService;
import org.example.spacecompany.service.StatisticsService;
import org.example.spacecompany.util.GameConfig;
import org.example.spacecompany.util.InputReader;

/**
 * Главный цикл консоли. Только ввод/вывод — каждое решение принимают сервисы,
 * здесь бизнес-логики нет.
 */
public class ConsoleMenu {

    private final Company company;
    private final FinanceService finance;
    private final RocketService rockets;
    private final EmployeeService employees;
    private final MissionService missions;
    private final StatisticsService statistics;
    private final SaveLoadService saveLoad;
    private final InputReader input;

    private final RocketMenu rocketMenu;
    private final EmployeeMenu employeeMenu;
    private final MissionMenu missionMenu;
    private final FinanceMenu financeMenu;
    private final StatisticsMenu statisticsMenu;

    public ConsoleMenu(Company company,
                       FinanceService finance,
                       RocketService rockets,
                       EmployeeService employees,
                       MissionService missions,
                       StatisticsService statistics,
                       SaveLoadService saveLoad,
                       InputReader input) {
        this.company = company;
        this.finance = finance;
        this.rockets = rockets;
        this.employees = employees;
        this.missions = missions;
        this.statistics = statistics;
        this.saveLoad = saveLoad;
        this.input = input;
        this.rocketMenu = new RocketMenu(rockets, input);
        this.employeeMenu = new EmployeeMenu(employees, input);
        this.missionMenu = new MissionMenu(missions, rockets, employees, input);
        this.financeMenu = new FinanceMenu(finance, employees, input);
        this.statisticsMenu = new StatisticsMenu(statistics, input);
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int choice = input.readInt("Выбор: ", 0, 9);
            try {
                switch (choice) {
                    case 1 -> showOverview();
                    case 2 -> rocketMenu.show();
                    case 3 -> employeeMenu.show();
                    case 4 -> missionMenu.show();
                    case 5 -> financeMenu.show();
                    case 6 -> statisticsMenu.show();
                    case 7 -> saveGame();
                    case 8 -> loadGame();
                    case 9 -> paySalaries();
                    case 0 -> exit = true;
                    default -> System.out.println("Неизвестный пункт.");
                }
            } catch (SpaceCompanyException | IllegalArgumentException e) {
                MenuIO.error(e.getMessage());
            }
        }
        System.out.println("Спасибо за игру! Через тернии — к звёздам.");
    }

    private void printMainMenu() {
        MenuIO.printHeader("СИМУЛЯТОР КОСМИЧЕСКОЙ КОМПАНИИ — " + company.getName());
        MenuIO.info(finance.balanceSummary()
                + " | ракет: " + company.getRockets().count()
                + ", сотрудников: " + company.getEmployees().count()
                + ", миссий: " + company.getMissions().count());
        System.out.println("1. Обзор компании");
        System.out.println("2. Ракеты");
        System.out.println("3. Сотрудники");
        System.out.println("4. Миссии");
        System.out.println("5. Финансы");
        System.out.println("6. Статистика");
        System.out.println("7. Сохранить игру");
        System.out.println("8. Загрузить игру");
        System.out.println("9. Выплатить зарплаты");
        System.out.println("0. Выход");
    }

    private void showOverview() {
        MenuIO.printHeader("Обзор компании");
        MenuIO.info(company.toString());
        MenuIO.info("Основана: " + company.getFoundedDate());
        MenuIO.info(finance.balanceSummary());
        MenuIO.info("--- недавние полёты ---");
        financeMenu.showLaunchHistory(company.getLaunchHistory());
        MenuIO.pause(input);
    }

    private void paySalaries() {
        employees.payMonthlySalaries();
        MenuIO.ok("Зарплаты выплачены. " + finance.balanceSummary());
    }

    private void saveGame() {
        String name = input.readLine("Файл сохранения [" + GameConfig.DEFAULT_SAVE_FILE + "]: ").strip();
        if (name.isEmpty()) {
            name = GameConfig.DEFAULT_SAVE_FILE;
        }
        Path file = Paths.get(name);
        try {
            saveLoad.save(company, file);
            MenuIO.ok("Игра сохранена: " + file.toAbsolutePath());
        } catch (GameSaveException e) {
            MenuIO.error("Не сохранилось: " + e.getMessage());
        }
    }

    private void loadGame() {
        String name = input.readLine("Файл сохранения [" + GameConfig.DEFAULT_SAVE_FILE + "]: ").strip();
        if (name.isEmpty()) {
            name = GameConfig.DEFAULT_SAVE_FILE;
        }
        Path file = Paths.get(name);
        if (!input.readYesNo("Загрузка заменит текущую игру. Продолжить?")) {
            return;
        }
        try {
            Company loaded = saveLoad.load(file);
            company.restoreFrom(loaded);
            MenuIO.ok("Игра загружена: " + company);
        } catch (GameSaveException e) {
            MenuIO.error("Не загрузилось: " + e.getMessage());
        }
    }
}
