package org.example.spacecompany.console;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionResult;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.domain.mission.MissionType;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.exception.SpaceCompanyException;
import org.example.spacecompany.service.EmployeeService;
import org.example.spacecompany.service.MissionService;
import org.example.spacecompany.service.MissionValidator;
import org.example.spacecompany.service.RocketService;
import org.example.spacecompany.util.InputReader;
import org.example.spacecompany.util.MoneyUtils;

/** Подменю «Миссии»: создание, экипаж, проверка, запуск (включая конкурентный). */
public class MissionMenu {

    private final MissionService missions;
    private final RocketService rockets;
    private final EmployeeService employees;
    private final InputReader input;

    public MissionMenu(MissionService missions, RocketService rockets,
                       EmployeeService employees, InputReader input) {
        this.missions = missions;
        this.rockets = rockets;
        this.employees = employees;
        this.input = input;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            MenuIO.printHeader("Миссии");
            System.out.println("1. Список миссий");
            System.out.println("2. Создать миссию");
            System.out.println("3. Назначить ракету и экипаж");
            System.out.println("4. Проверить готовность");
            System.out.println("5. Запустить миссию");
            System.out.println("6. Запустить ВСЕ готовые миссии сразу");
            System.out.println("7. Отменить миссию");
            System.out.println("8. Детали миссии / бортовой журнал");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ", 0, 8);
            try {
                switch (choice) {
                    case 1 -> listMissions();
                    case 2 -> createMission();
                    case 3 -> assignRocketAndCrew();
                    case 4 -> checkReadiness();
                    case 5 -> launchOne();
                    case 6 -> launchAllConcurrently();
                    case 7 -> cancelMission();
                    case 8 -> showDetails();
                    case 0 -> back = true;
                    default -> System.out.println("Неизвестный пункт.");
                }
            } catch (SpaceCompanyException | IllegalArgumentException e) {
                MenuIO.error(e.getMessage());
            }
        }
    }

    private void listMissions() {
        List<Mission> all = missions.findAll();
        if (all.isEmpty()) {
            MenuIO.info("Миссий пока нет. Создайте первую!");
            return;
        }
        for (Mission mission : all) {
            MenuIO.info("[" + MenuIO.shortId(mission.getId()) + "] " + mission);
        }
    }

    private void createMission() {
        MenuIO.info("Типы миссий:");
        MissionType[] types = MissionType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("%d. %s — %s%n", i + 1, types[i], types[i].getDescription());
        }
        int choice = input.readInt("Тип (1-" + types.length + "): ", 1, types.length);
        MissionType type = types[choice - 1];
        String name = input.readNonBlankLine("Название миссии: ");
        Mission mission;
        if (input.readYesNo("Взять стандартные груз (" + type.getRequiredPayloadKg()
                + " кг) и награду (" + MoneyUtils.format(type.getBaseReward()) + ")?")) {
            mission = missions.createMission(name, type);
        } else {
            int payload = input.readInt("Нужная грузоподъёмность (кг, 0-20000): ", 0, 20000);
            BigDecimal reward = input.readMoney("Награда: ");
            mission = missions.createMission(name, type, payload, reward);
        }
        MenuIO.info("Для миссии «" + type.getDisplayName() + "» нужны роли: "
                + MissionValidator.requiredRolesFor(type));
        MenuIO.ok("Миссия «" + mission.getName() + "» создана (PLANNED).");
    }

    private void assignRocketAndCrew() {
        List<Mission> editable = missions.findAll().stream()
                .filter(m -> !m.getStatus().isTerminal() && m.getStatus() != MissionStatus.IN_PROGRESS)
                .toList();
        Mission mission = MenuIO.choose(editable, "миссию", input,
                m -> m.getName() + " [" + m.getStatus() + "]");
        if (mission == null) {
            return;
        }
        Rocket rocket = MenuIO.choose(rockets.findAll(), "ракету", input,
                r -> r.getName() + " [" + r.getStatus() + ", грузоподъёмность "
                        + r.getPayloadCapacityKg() + " кг]");
        if (rocket == null) {
            return;
        }
        missions.assignRocket(mission.getId(), rocket.getId());
        MenuIO.ok(rocket.getName() + " назначена на «" + mission.getName() + "».");

        MenuIO.info("Набираем экипаж (0 — хватит). Нужно минимум "
                + mission.getType().getMinCrewSize() + " чел., роли: "
                + MissionValidator.requiredRolesFor(mission.getType()));
        while (true) {
            List<Employee> available = new ArrayList<>(employees.findAll().stream()
                    .filter(e -> e.isAvailable() && !mission.getCrewIds().contains(e.getId()))
                    .toList());
            if (available.isEmpty()) {
                MenuIO.info("Свободных сотрудников больше нет.");
                break;
            }
            Employee picked = MenuIO.choose(available, "члена экипажа", input,
                    e -> e.getName() + " [" + e.getRole() + ", навык " + e.getSkillLevel() + "]");
            if (picked == null) {
                break;
            }
            missions.assignCrewMember(mission.getId(), picked.getId());
            MenuIO.ok(picked.getName() + " в экипаже (всего: " + mission.crewSize() + ").");
            if (!input.readYesNo("Добавить ещё одного?")) {
                break;
            }
        }
        boolean ready = missions.refreshReadiness(mission.getId());
        MenuIO.info(ready ? "Миссия ГОТОВА к запуску!"
                : "Миссия пока PLANNED (не все требования выполнены).");
    }

    private void checkReadiness() {
        Mission mission = MenuIO.choose(missions.findAll(), "миссию", input,
                m -> m.getName() + " [" + m.getStatus() + "]");
        if (mission == null) {
            return;
        }
        boolean ready = missions.refreshReadiness(mission.getId());
        MenuIO.info(ready ? "«" + mission.getName() + "» ГОТОВА."
                : "«" + mission.getName() + "» НЕ готова. Нужны заправленная ракета и полный экипаж.");
    }

    private void launchOne() {
        List<Mission> ready = missions.findReady();
        Mission mission = MenuIO.choose(ready, "ГОТОВУЮ миссию", input,
                m -> m.getName() + " [" + m.getType().getDisplayName()
                        + ", награда " + MoneyUtils.format(m.getReward()) + "]");
        if (mission == null) {
            return;
        }
        MenuIO.info("Зажигание...");
        MissionResult result = missions.launch(mission.getId());
        MenuIO.info(result.success() ? "[УСПЕХ] " + result.message() : "[ПРОВАЛ] " + result.message());
        MenuIO.info("Награда: " + MoneyUtils.format(result.rewardPaid())
                + ", доп. расходы: " + MoneyUtils.format(result.extraCost())
                + ", время полёта: " + formatDuration(result));
        MenuIO.info("--- бортовой журнал ---\n" + mission.getResultLog());
    }

    private void launchAllConcurrently() {
        List<Mission> ready = missions.findReady();
        if (ready.isEmpty()) {
            MenuIO.info("Нет ГОТОВЫХ миссий.");
            return;
        }
        MenuIO.info("Запускаем " + ready.size() + " миссий одновременно...");
        List<Future<MissionResult>> futures = missions.launchAllReady();
        for (int i = 0; i < futures.size(); i++) {
            try {
                MissionResult result = futures.get(i).get();
                MenuIO.info((i + 1) + ". " + (result.success() ? "[УСПЕХ] " : "[ПРОВАЛ] ")
                        + result.message() + " (итого " + MoneyUtils.format(result.netProfit()) + ")");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                MenuIO.error("Ожидание полётов прервано.");
                return;
            } catch (ExecutionException e) {
                MenuIO.error("Полёт завершился ошибкой: " + e.getCause().getMessage());
            }
        }
    }

    private void cancelMission() {
        List<Mission> cancellable = missions.findAll().stream()
                .filter(m -> !m.getStatus().isTerminal() && m.getStatus() != MissionStatus.IN_PROGRESS)
                .toList();
        Mission mission = MenuIO.choose(cancellable, "миссию для отмены", input,
                m -> m.getName() + " [" + m.getStatus() + "]");
        if (mission == null) {
            return;
        }
        if (input.readYesNo("Отменить «" + mission.getName() + "»?")) {
            missions.cancelMission(mission.getId());
            MenuIO.ok("Миссия отменена.");
        }
    }

    private void showDetails() {
        Mission mission = MenuIO.choose(missions.findAll(), "миссию", input,
                m -> m.getName() + " [" + m.getStatus() + "]");
        if (mission == null) {
            return;
        }
        MenuIO.info(mission.toString());
        if (mission.getResultLog() != null) {
            MenuIO.info("--- бортовой журнал ---\n" + mission.getResultLog());
        } else {
            MenuIO.info("Журнала пока нет (миссия ещё не летала).");
        }
    }

    private static String formatDuration(MissionResult result) {
        long hours = result.duration().toHours();
        if (hours < 24) {
            return hours + " ч";
        }
        return (hours / 24) + " д " + (hours % 24) + " ч";
    }
}
