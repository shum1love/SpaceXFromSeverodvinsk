package org.example.spacecompany.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.spacecompany.GameSession;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.RocketStatus;
import org.example.spacecompany.util.MoneyUtils;

/**
 * Экран «Ракеты»: таблица флота + покупка/заправка/ремонт/обслуживание.
 * Решения — в {@code RocketService}; здесь вызовы через {@code attempt}
 * и обновление таблицы. Статусы рисуются цветными пилюлями ({@code Cells}).
 */
public class RocketsController extends BaseViewController {

    @FXML
    private TableView<Rocket> table;

    @FXML
    private TableColumn<Rocket, String> nameColumn;

    @FXML
    private TableColumn<Rocket, String> modelColumn;

    @FXML
    private TableColumn<Rocket, String> statusColumn;

    @FXML
    private TableColumn<Rocket, String> fuelColumn;

    @FXML
    private TableColumn<Rocket, String> conditionColumn;

    @FXML
    private TableColumn<Rocket, String> reliabilityColumn;

    @FXML
    private TableColumn<Rocket, String> flightsColumn;

    public RocketsController(GameSession session, Runnable changed) {
        super(session, changed);
    }

    @FXML
    public void initialize() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Cells.text(nameColumn, Rocket::getName);
        Cells.text(modelColumn, rocket -> rocket.getModel().getDisplayName());
        Cells.pill(statusColumn,
                rocket -> Cells.rocketName(rocket.getStatus()),
                rocket -> Cells.rocketPill(rocket.getStatus()));
        Cells.coloredText(fuelColumn,
                rocket -> rocket.getFuel() + "/" + rocket.getMaxFuel(),
                rocket -> {
                    if (rocket.getFuel() >= rocket.getMaxFuel()) {
                        return javafx.scene.paint.Color.web("#3fce7a");
                    }
                    if (rocket.getFuel() > 0) {
                        return javafx.scene.paint.Color.web("#f0a63c");
                    }
                    return javafx.scene.paint.Color.web("#f0564d");
                });
        Cells.text(conditionColumn, rocket -> rocket.getCondition() + "%");
        Cells.text(reliabilityColumn, rocket -> "%.2f".formatted(rocket.getReliability()));
        Cells.text(flightsColumn, rocket -> rocket.getSuccessCount() + "/" + rocket.getLaunchCount());
    }

    @Override
    public void refresh() {
        table.setItems(FXCollections.observableArrayList(session.rockets().findAll()));
    }

    @FXML
    private void buy() {
        Dialogs.buyRocket().ifPresent(choice -> {
            RocketModel model = choice.getKey();
            String name = choice.getValue();
            attempt(() -> session.rockets().buyRocket(model, name.isBlank() ? null : name),
                    "Куплена ракета " + model.getDisplayName()
                            + ". Баланс: " + MoneyUtils.format(session.finance().getBalance()),
                    "Ракеты");
        });
    }

    @FXML
    private void refuel() {
        Rocket rocket = requireSelection(table, "ракету");
        if (rocket == null) {
            return;
        }
        attempt(() -> session.rockets().refuelRocket(rocket.getId()),
                rocket.getName() + " заправлена.", "Ракеты");
    }

    @FXML
    private void repair() {
        Rocket rocket = requireSelection(table, "ракету");
        if (rocket == null) {
            return;
        }
        attempt(() -> session.rockets().repairRocket(rocket.getId()),
                rocket.getName() + " отремонтирована.", "Ракеты");
    }

    @FXML
    private void maintenance() {
        Rocket rocket = requireSelection(table, "ракету");
        if (rocket == null) {
            return;
        }
        if (rocket.getStatus() == RocketStatus.IN_MAINTENANCE) {
            attempt(() -> session.rockets().finishMaintenance(rocket.getId()),
                    rocket.getName() + " снята с обслуживания.", "Ракеты");
        } else {
            attempt(() -> session.rockets().sendToMaintenance(rocket.getId()),
                    rocket.getName() + " отправлена на обслуживание.", "Ракеты");
        }
    }
}
