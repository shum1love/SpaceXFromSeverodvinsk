package org.example.spacecompany.console;

import java.util.List;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.exception.SpaceCompanyException;
import org.example.spacecompany.service.RocketService;
import org.example.spacecompany.util.InputReader;
import org.example.spacecompany.util.MoneyUtils;

/** Подменю «Ракеты»: покупка, заправка, ремонт, осмотр. */
public class RocketMenu {

    private final RocketService rockets;
    private final InputReader input;

    public RocketMenu(RocketService rockets, InputReader input) {
        this.rockets = rockets;
        this.input = input;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            MenuIO.printHeader("Ракеты");
            System.out.println("1. Список ракет");
            System.out.println("2. Ракеты по надёжности");
            System.out.println("3. Купить ракету");
            System.out.println("4. Заправить ракету");
            System.out.println("5. Отремонтировать ракету");
            System.out.println("6. Техобслуживание");
            System.out.println("7. Самая дорогая ракета");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ", 0, 7);
            try {
                switch (choice) {
                    case 1 -> listRockets();
                    case 2 -> listSortedByReliability();
                    case 3 -> buyRocket();
                    case 4 -> refuelRocket();
                    case 5 -> repairRocket();
                    case 6 -> maintenance();
                    case 7 -> showMostExpensive();
                    case 0 -> back = true;
                    default -> System.out.println("Неизвестный пункт.");
                }
            } catch (SpaceCompanyException | IllegalArgumentException e) {
                MenuIO.error(e.getMessage());
            }
        }
    }

    private void listRockets() {
        List<Rocket> all = rockets.findAll();
        if (all.isEmpty()) {
            MenuIO.info("Ракет пока нет. Купите первую!");
            return;
        }
        for (Rocket rocket : all) {
            MenuIO.info("[" + MenuIO.shortId(rocket.getId()) + "] " + rocket);
        }
    }

    private void listSortedByReliability() {
        List<Rocket> sorted = rockets.sortByReliabilityDesc();
        if (sorted.isEmpty()) {
            MenuIO.info("Ракет пока нет.");
            return;
        }
        MenuIO.info("Ракеты по надёжности (лучшие сверху):");
        for (Rocket rocket : sorted) {
            MenuIO.info("  " + rocket.getName() + " — надёжность "
                    + "%.2f".formatted(rocket.getReliability())
                    + ", успешность " + MoneyUtils.formatPercent(rocket.getSuccessRate()));
        }
    }

    private void buyRocket() {
        MenuIO.info("Доступные модели:");
        RocketModel[] models = RocketModel.values();
        for (int i = 0; i < models.length; i++) {
            System.out.printf("%d. %s%n", i + 1, models[i]);
        }
        int choice = input.readInt("Модель (1-" + models.length + "): ", 1, models.length);
        String name = input.readLine("Своё название (Enter — по умолчанию): ").strip();
        Rocket bought = rockets.buyRocket(models[choice - 1], name.isEmpty() ? null : name);
        MenuIO.ok("Куплена " + bought.getName() + ". Не забудьте заправить!");
    }

    private void refuelRocket() {
        Rocket rocket = MenuIO.choose(rockets.findAll(), "ракету", input,
                r -> r.getName() + " [" + r.getStatus() + ", топливо " + r.getFuel() + "/" + r.getMaxFuel() + "]");
        if (rocket == null) {
            return;
        }
        rockets.refuelRocket(rocket.getId());
        MenuIO.ok(rocket.getName() + " заправлена: " + rocket.getFuel() + "/" + rocket.getMaxFuel());
    }

    private void repairRocket() {
        Rocket rocket = MenuIO.choose(rockets.findAll(), "ракету", input,
                r -> r.getName() + " [" + r.getStatus() + ", состояние " + r.getCondition() + "%]");
        if (rocket == null) {
            return;
        }
        rockets.repairRocket(rocket.getId());
        MenuIO.ok(rocket.getName() + " отремонтирована до 100%.");
    }

    private void maintenance() {
        Rocket rocket = MenuIO.choose(rockets.findAll(), "ракету", input,
                r -> r.getName() + " [" + r.getStatus() + "]");
        if (rocket == null) {
            return;
        }
        switch (rocket.getStatus()) {
            case IN_MAINTENANCE -> {
                rockets.finishMaintenance(rocket.getId());
                MenuIO.ok(rocket.getName() + " снята с обслуживания.");
            }
            case DESTROYED -> MenuIO.error("Уничтоженную ракету обслуживать нельзя.");
            case ON_MISSION -> MenuIO.error("Ракета сейчас на миссии.");
            default -> {
                rockets.sendToMaintenance(rocket.getId());
                MenuIO.ok(rocket.getName() + " отправлена на техобслуживание.");
            }
        }
    }

    private void showMostExpensive() {
        rockets.findMostExpensive().ifPresentOrElse(
                r -> MenuIO.info("Самая дорогая: " + r.getName()
                        + " (" + MoneyUtils.format(r.getModel().getPrice()) + ")"),
                () -> MenuIO.info("Ракет пока нет."));
    }
}
