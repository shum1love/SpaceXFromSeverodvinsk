package org.example.spacecompany.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.example.spacecompany.GameSession;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionResult;
import org.example.spacecompany.domain.mission.MissionStatus;
import org.example.spacecompany.exception.SpaceCompanyException;
import org.example.spacecompany.util.MoneyUtils;

/**
 * Экран «Миссии»: создание, сборка экипажа, запуски.
 *
 * <p>Учебный пример JavaFX-concurrency (правила игры):
 * <ul>
 *   <li>долгая работа (полёт) — только в фоне, никогда в FX-потоке, иначе окно «замрёт»;</li>
 *   <li>{@code Task} выполняется в фоновом потоке, а его колбэки
 *       ({@code setOnSucceeded}/{@code setOnFailed}) — уже в FX-потоке, там можно трогать интерфейс;</li>
 *   <li>сам полёт считает {@code MissionService} в своём пуле ({@code Future}),
 *       задача лишь ждёт {@code future.get()} в фоне и приносит итог на FX-поток.</li>
 * </ul>
 */
public class MissionsController extends BaseViewController {

    @FXML
    private TableView<Mission> table;

    @FXML
    private TableColumn<Mission, String> nameColumn;

    @FXML
    private TableColumn<Mission, String> typeColumn;

    @FXML
    private TableColumn<Mission, String> statusColumn;

    @FXML
    private TableColumn<Mission, String> rewardColumn;

    @FXML
    private TableColumn<Mission, String> crewColumn;

    @FXML
    private ComboBox<Rocket> rocketCombo;

    @FXML
    private ListView<Employee> crewList;

    @FXML
    private Label readinessLabel;

    @FXML
    private ProgressIndicator launchProgress;

    @FXML
    private TextArea flightLog;

    public MissionsController(GameSession session, Runnable changed) {
        super(session, changed);
    }

    @FXML
    public void initialize() {
        Cells.text(nameColumn, Mission::getName);
        Cells.text(typeColumn, mission -> mission.getType().getDisplayName());
        Cells.pill(statusColumn,
                mission -> Cells.missionName(mission.getStatus()),
                mission -> Cells.missionPill(mission.getStatus()));
        Cells.text(rewardColumn, mission -> MoneyUtils.format(mission.getReward()));
        Cells.text(crewColumn, mission -> mission.crewSize() + " чел.");

        rocketCombo.setCellFactory(list -> rocketCell());
        rocketCombo.setButtonCell(rocketCell());
        rocketCombo.setOnAction(event -> assignSelectedRocket());

        crewList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        crewList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                setText(empty || employee == null ? null
                        : employee.getName() + " [" + Cells.roleName(employee.getRole())
                        + ", навык " + employee.getSkillLevel()
                        + ", " + Cells.employeeName(employee.getStatus()) + "]");
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, mission) -> showDetails(mission));
        launchProgress.setVisible(false);
    }

    @Override
    public void refresh() {
        table.setItems(FXCollections.observableArrayList(session.missions().findAll()));
        rocketCombo.setItems(FXCollections.observableArrayList(session.rockets().findAll()));
        crewList.setItems(FXCollections.observableArrayList(session.employees().findAll()));
        showDetails(table.getSelectionModel().getSelectedItem());
    }

    private void showDetails(Mission mission) {
        if (mission == null) {
            rocketCombo.setValue(null);
            crewList.getSelectionModel().clearSelection();
            readinessLabel.setText("Выберите миссию в таблице.");
            return;
        }
        for (Rocket rocket : rocketCombo.getItems()) {
            if (rocket.getId().equals(mission.getAssignedRocketId())) {
                rocketCombo.setValue(rocket);
                break;
            }
        }
        crewList.getSelectionModel().clearSelection();
        for (int i = 0; i < crewList.getItems().size(); i++) {
            if (mission.getCrewIds().contains(crewList.getItems().get(i).getId())) {
                crewList.getSelectionModel().select(i);
            }
        }
        updateReadiness(mission);
    }

    private void updateReadiness(Mission mission) {
        try {
            boolean ready = session.missions().refreshReadiness(mission.getId());
            readinessLabel.setText(ready ? "✅ ГОТОВА к запуску" : "⬜ Не готова: нужна заправленная ракета и полный экипаж");
        } catch (SpaceCompanyException | IllegalArgumentException e) {
            readinessLabel.setText("⬜ " + e.getMessage());
        }
    }

    private void assignSelectedRocket() {
        Mission mission = table.getSelectionModel().getSelectedItem();
        Rocket rocket = rocketCombo.getValue();
        if (mission == null || rocket == null) {
            return;
        }
        // Программная установка значения при обновлении экрана — не действие игрока.
        if (mission.getStatus().isTerminal() || mission.getStatus() == MissionStatus.IN_PROGRESS) {
            return;
        }
        if (rocket.getId().equals(mission.getAssignedRocketId())) {
            return;
        }
        try {
            session.missions().assignRocket(mission.getId(), rocket.getId());
            afterChangeSilent();
            updateReadiness(mission);
        } catch (SpaceCompanyException | IllegalArgumentException e) {
            UiUtils.error(e.getMessage());
            refresh();
        }
    }

    @FXML
    private void saveCrew() {
        Mission mission = requireSelection(table, "миссию");
        if (mission == null) {
            return;
        }
        try {
            Set<UUID> wanted = new HashSet<>();
            for (Employee employee : crewList.getSelectionModel().getSelectedItems()) {
                wanted.add(employee.getId());
            }
            for (UUID id : List.copyOf(mission.getCrewIds())) {
                if (!wanted.contains(id)) {
                    session.missions().removeCrewMember(mission.getId(), id);
                }
            }
            for (UUID id : wanted) {
                if (!mission.getCrewIds().contains(id)) {
                    session.missions().assignCrewMember(mission.getId(), id);
                }
            }
            afterChange("Миссии", "Экипаж сохранён: " + wanted.size() + " чел.");
            updateReadiness(mission);
        } catch (SpaceCompanyException | IllegalArgumentException e) {
            UiUtils.error(e.getMessage());
        }
        refresh();
    }

    @FXML
    private void create() {
        Dialogs.createMission().ifPresent(data -> attempt(() -> {
            if (data.customPayload() == null) {
                session.missions().createMission(data.name(), data.type());
            } else {
                session.missions().createMission(data.name(), data.type(),
                        data.customPayload(), data.customReward());
            }
        }, "Миссия «" + data.name() + "» создана.", "Миссии"));
    }

    @FXML
    private void launch() {
        Mission mission = requireSelection(table, "миссию");
        if (mission == null) {
            return;
        }
        try {
            if (!session.missions().refreshReadiness(mission.getId())) {
                UiUtils.error("Миссия не готова: назначьте заправленную ракету и полный экипаж.");
                return;
            }
        } catch (SpaceCompanyException | IllegalArgumentException e) {
            UiUtils.error(e.getMessage());
            return;
        }

        UUID missionId = mission.getId();
        setBusy(true);
        // Фоновая задача: ждёт Future полёта НЕ в FX-потоке.
        Task<MissionResult> flight = new Task<>() {
            @Override
            protected MissionResult call() throws Exception {
                Future<MissionResult> future = session.missions().launchAsync(missionId);
                return future.get();
            }
        };
        // А эти колбэки JavaFX вызовет уже в FX-потоке — тут можно в интерфейс.
        flight.setOnSucceeded(event -> {
            setBusy(false);
            MissionResult result = flight.getValue();
            afterChangeSilent();
            flightLog.setText(session.missions().findById(missionId)
                    .map(Mission::getResultLog).orElse(""));
            UiUtils.info(result.success() ? "Успех!" : "Провал",
                    result.message() + "\nНаграда: " + MoneyUtils.format(result.rewardPaid())
                            + ", доп. расходы: " + MoneyUtils.format(result.extraCost()));
        });
        flight.setOnFailed(event -> {
            setBusy(false);
            refresh();
            Throwable cause = flight.getException();
            UiUtils.error("Полёт сорвался с ошибкой: "
                    + (cause.getCause() != null ? cause.getCause().getMessage() : cause.getMessage()));
        });
        Thread thread = new Thread(flight, "flight-" + missionId.toString().substring(0, 8));
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void launchAll() {
        var ready = session.missions().findReady();
        if (ready.isEmpty()) {
            UiUtils.error("Нет готовых миссий.");
            return;
        }
        setBusy(true);
        // Одна фоновая задача ждёт ВСЕ полёты: веерный запуск из MissionService.
        Task<List<MissionResult>> flights = new Task<>() {
            @Override
            protected List<MissionResult> call() throws Exception {
                List<Future<MissionResult>> futures = session.missions().launchAllReady();
                List<MissionResult> results = new java.util.ArrayList<>();
                int done = 0;
                for (Future<MissionResult> future : futures) {
                    results.add(future.get());
                    updateProgress(++done, futures.size());
                }
                return results;
            }
        };
        launchProgress.progressProperty().bind(flights.progressProperty());
        flights.setOnSucceeded(event -> {
            launchProgress.progressProperty().unbind();
            setBusy(false);
            afterChangeSilent();
            long wins = flights.getValue().stream().filter(MissionResult::success).count();
            UiUtils.info("Полёты завершены",
                    "Успешно: " + wins + " из " + flights.getValue().size());
        });
        flights.setOnFailed(event -> {
            launchProgress.progressProperty().unbind();
            setBusy(false);
            refresh();
            UiUtils.error("Групповой запуск сорвался: " + flights.getException().getMessage());
        });
        Thread thread = new Thread(flights, "flights-all");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void cancel() {
        Mission mission = requireSelection(table, "миссию");
        if (mission == null) {
            return;
        }
        if (!UiUtils.confirm("Отменить миссию «" + mission.getName() + "»?")) {
            return;
        }
        attempt(() -> session.missions().cancelMission(mission.getId()),
                "Миссия отменена.", "Миссии");
    }

    private void setBusy(boolean busy) {
        // Вызывается из FX-потока (колбэки) и из обработчиков кнопок (тоже FX-поток).
        assert Platform.isFxApplicationThread();
        launchProgress.setVisible(busy);
        if (!busy) {
            launchProgress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        }
    }

    private static ListCell<Rocket> rocketCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Rocket rocket, boolean empty) {
                super.updateItem(rocket, empty);
                setText(empty || rocket == null ? null
                        : rocket.getName() + " [" + Cells.rocketName(rocket.getStatus()) + "]");
            }
        };
    }
}
