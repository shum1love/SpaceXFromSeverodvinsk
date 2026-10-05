package org.example.spacecompany.ui;

import java.math.BigDecimal;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.example.spacecompany.domain.RocketModel;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;
import org.example.spacecompany.domain.employee.Engineer;
import org.example.spacecompany.domain.employee.MissionSpecialist;
import org.example.spacecompany.domain.employee.Pilot;
import org.example.spacecompany.domain.employee.Scientist;
import org.example.spacecompany.domain.mission.MissionType;

/**
 * Формы-диалоги: покупка ракеты, найм, создание миссии.
 *
 * <p>Диалоги только собирают ввод и отдают данные; создают сущности и двигают
 * деньги сервисы, которые вызывают контроллеры. Проверки чисел — здесь же,
 * у самого поля (некорректный ввод просто не даёт закрыть окно кнопкой OK).
 */
public final class Dialogs {

    private Dialogs() {
    }

    /** Выбор модели + название. Пустое название — ракета назовётся сама. */
    public static Optional<Pair<RocketModel, String>> buyRocket() {
        Dialog<Pair<RocketModel, String>> dialog = new Dialog<>();
        dialog.setTitle("Покупка ракеты");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styled(dialog);

        ComboBox<RocketModel> models = new ComboBox<>();
        models.getItems().addAll(RocketModel.values());
        models.setValue(RocketModel.FALCON_9);
        models.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(RocketModel model, boolean empty) {
                super.updateItem(model, empty);
                setText(empty || model == null ? null : model.getDisplayName() + " — " + model.getPrice() + " ₽");
            }
        });
        models.setButtonCell(models.getCellFactory().call(null));
        TextField name = new TextField();
        name.setPromptText("Название (необязательно)");

        dialog.getDialogPane().setContent(form(
                new Label("Модель:"), models,
                new Label("Название:"), name));
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Pair<>(models.getValue(), name.getText().strip()) : null);
        return dialog.showAndWait();
    }

    /** Данные для найма. Возвращает готового сотрудника (ещё не нанятого!). */
    public static Optional<Employee> hireEmployee() {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle("Найм сотрудника");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styled(dialog);

        ComboBox<EmployeeRole> roles = new ComboBox<>();
        roles.getItems().addAll(EmployeeRole.values());
        roles.setValue(EmployeeRole.PILOT);
        TextField name = new TextField();
        name.setPromptText("Имя");
        TextField salary = new TextField("1000");
        TextField experience = new TextField("2");
        TextField skill = new TextField("5");

        dialog.getDialogPane().setContent(form(
                new Label("Профессия:"), roles,
                new Label("Имя:"), name,
                new Label("Зарплата:"), salary,
                new Label("Опыт (лет):"), experience,
                new Label("Навык (1-10):"), skill));
        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            try {
                String employeeName = name.getText().strip();
                BigDecimal employeeSalary = new BigDecimal(salary.getText().strip());
                int exp = Integer.parseInt(experience.getText().strip());
                int sk = Integer.parseInt(skill.getText().strip());
                return switch (roles.getValue()) {
                    case ENGINEER -> new Engineer(employeeName, employeeSalary, exp, sk);
                    case PILOT -> new Pilot(employeeName, employeeSalary, exp, sk);
                    case SCIENTIST -> new Scientist(employeeName, employeeSalary, exp, sk);
                    case MISSION_SPECIALIST -> new MissionSpecialist(employeeName, employeeSalary, exp, sk);
                };
            } catch (IllegalArgumentException e) {
                UiUtils.error("Проверьте поля: " + e.getMessage());
                return null;
            }
        });
        return dialog.showAndWait();
    }

    /** Данные новой миссии. Пустой customPayload — взять стандартные параметры типа. */
    public record MissionData(String name, MissionType type, Integer customPayload, BigDecimal customReward) {
    }

    public static Optional<MissionData> createMission() {
        Dialog<MissionData> dialog = new Dialog<>();
        dialog.setTitle("Новая миссия");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styled(dialog);

        TextField name = new TextField();
        name.setPromptText("Название миссии");
        ComboBox<MissionType> types = new ComboBox<>();
        types.getItems().addAll(MissionType.values());
        types.setValue(MissionType.SATELLITE_LAUNCH);
        types.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(MissionType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getDisplayName());
            }
        });
        types.setButtonCell(types.getCellFactory().call(null));
        CheckBox standard = new CheckBox("Стандартные груз и награда");
        standard.setSelected(true);
        TextField payload = new TextField("500");
        TextField reward = new TextField("3000");
        payload.disableProperty().bind(standard.selectedProperty());
        reward.disableProperty().bind(standard.selectedProperty());

        dialog.getDialogPane().setContent(form(
                new Label("Название:"), name,
                new Label("Тип:"), types,
                standard, new Label(""),
                new Label("Груз (кг):"), payload,
                new Label("Награда:"), reward));
        dialog.setResultConverter(button -> {
            if (button != ButtonType.OK) {
                return null;
            }
            try {
                String missionName = name.getText().strip();
                if (missionName.isEmpty()) {
                    UiUtils.error("Название миссии не должно быть пустым.");
                    return null;
                }
                if (standard.isSelected()) {
                    return new MissionData(missionName, types.getValue(), null, null);
                }
                return new MissionData(missionName, types.getValue(),
                        Integer.parseInt(payload.getText().strip()),
                        new BigDecimal(reward.getText().strip()));
            } catch (IllegalArgumentException e) {
                UiUtils.error("Проверьте груз и награду: " + e.getMessage());
                return null;
            }
        });
        return dialog.showAndWait();
    }

    private static GridPane form(javafx.scene.Node... nodes) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));
        for (int i = 0; i < nodes.length; i += 2) {
            grid.add(nodes[i], 0, i / 2);
            grid.add(nodes[i + 1], 1, i / 2);
        }
        return grid;
    }

    private static void styled(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(
                Dialogs.class.getResource("/ui/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("dialog");
    }
}
