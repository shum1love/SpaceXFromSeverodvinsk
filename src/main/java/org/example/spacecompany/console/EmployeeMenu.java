package org.example.spacecompany.console;

import java.math.BigDecimal;
import java.util.List;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.MissionSpecialist;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.employee.Scientist;
import org.example.spacecompany.exception.SpaceCompanyException;
import org.example.spacecompany.service.EmployeeService;
import org.example.spacecompany.util.InputReader;
import org.example.spacecompany.util.MoneyUtils;

/** Подменю «Сотрудники»: найм, увольнение, обучение, зарплаты. */
public class EmployeeMenu {

    private final EmployeeService employees;
    private final InputReader input;

    public EmployeeMenu(EmployeeService employees, InputReader input) {
        this.employees = employees;
        this.input = input;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            MenuIO.printHeader("Сотрудники");
            System.out.println("1. Список сотрудников");
            System.out.println("2. Нанять сотрудника");
            System.out.println("3. Уволить сотрудника");
            System.out.println("4. Обучить сотрудника (+1 к навыку)");
            System.out.println("5. Отпуск / возврат из отпуска");
            System.out.println("6. Выплатить зарплаты сейчас");
            System.out.println("7. Лучший сотрудник");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ", 0, 7);
            try {
                switch (choice) {
                    case 1 -> listEmployees();
                    case 2 -> hireEmployee();
                    case 3 -> fireEmployee();
                    case 4 -> trainEmployee();
                    case 5 -> toggleLeave();
                    case 6 -> paySalaries();
                    case 7 -> showBest();
                    case 0 -> back = true;
                    default -> System.out.println("Неизвестный пункт.");
                }
            } catch (SpaceCompanyException | IllegalArgumentException e) {
                MenuIO.error(e.getMessage());
            }
        }
    }

    private void listEmployees() {
        List<Employee> all = employees.findAll();
        if (all.isEmpty()) {
            MenuIO.info("Сотрудников пока нет. Наймите кого-нибудь!");
            return;
        }
        for (Employee employee : all) {
            MenuIO.info("[" + MenuIO.shortId(employee.getId()) + "] " + employee
                    + " — " + employee.describeDuties());
        }
    }

    private void hireEmployee() {
        MenuIO.info("Профессии:");
        EmployeeRole[] roles = EmployeeRole.values();
        for (int i = 0; i < roles.length; i++) {
            System.out.printf("%d. %s%n", i + 1, roles[i]);
        }
        int roleChoice = input.readInt("Профессия (1-" + roles.length + "): ", 1, roles.length);
        EmployeeRole role = roles[roleChoice - 1];
        String name = input.readNonBlankLine("Имя: ");
        BigDecimal salary = input.readMoney("Зарплата в месяц: ");
        int experience = input.readInt("Опыт (лет, 0-40): ", 0, 40);
        int skill = input.readInt("Навык (1-10): ", 1, 10);

        Employee employee = switch (role) {
            case ENGINEER -> new Engineer(name, salary, experience, skill);
            case PILOT -> new Pilot(name, salary, experience, skill);
            case SCIENTIST -> new Scientist(name, salary, experience, skill);
            case MISSION_SPECIALIST -> new MissionSpecialist(name, salary, experience, skill);
        };
        employees.hire(employee);
        MenuIO.ok("Нанят(а): " + employee.getName() + " (" + role + "). " + employee.work());
    }

    private void fireEmployee() {
        Employee employee = MenuIO.choose(employees.findAll(), "сотрудника", input,
                e -> e.getName() + " [" + e.getRole() + ", " + e.getStatus() + "]");
        if (employee == null) {
            return;
        }
        if (input.readYesNo("Уволить " + employee.getName() + "?")) {
            employees.fire(employee.getId());
            MenuIO.ok(employee.getName() + " уволен(а).");
        }
    }

    private void trainEmployee() {
        Employee employee = MenuIO.choose(employees.findAll(), "сотрудника", input,
                e -> e.getName() + " [навык " + e.getSkillLevel() + "]");
        if (employee == null) {
            return;
        }
        employees.train(employee.getId());
        MenuIO.ok(employee.getName() + " обучен(а), навык: " + employee.getSkillLevel() + ".");
    }

    private void toggleLeave() {
        Employee employee = MenuIO.choose(employees.findAll(), "сотрудника", input,
                e -> e.getName() + " [" + e.getStatus() + "]");
        if (employee == null) {
            return;
        }
        switch (employee.getStatus()) {
            case ON_LEAVE -> {
                employees.returnFromLeave(employee.getId());
                MenuIO.ok(employee.getName() + " вернулся и доступен.");
            }
            case AVAILABLE -> {
                employees.sendOnLeave(employee.getId());
                MenuIO.ok(employee.getName() + " ушёл в отпуск.");
            }
            default -> MenuIO.error(employee.getName() + " сейчас на миссии.");
        }
    }

    private void paySalaries() {
        BigDecimal total = employees.payMonthlySalaries();
        MenuIO.ok("Выплачено зарплат на " + MoneyUtils.format(total) + ".");
    }

    private void showBest() {
        employees.findBestEmployee().ifPresentOrElse(
                e -> MenuIO.info("Лучший сотрудник: " + e.getName()
                        + " [" + e.getRole() + ", навык " + e.getSkillLevel()
                        + ", опыт " + e.getExperienceYears() + " л.]"),
                () -> MenuIO.info("Сотрудников пока нет."));
    }
}
