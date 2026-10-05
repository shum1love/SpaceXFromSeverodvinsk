package org.example.spacecompany.ui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.example.spacecompany.GameSession;

/**
 * Корневой контроллер: боковое меню + центр + строка состояния.
 * Переключает экраны, сам ничего не решает — только загружает FXML
 * и просит экран обновиться. Сессия одна на всё приложение.
 */
public class MainController {

    private final GameSession session;

    @FXML
    private BorderPane root;

    @FXML
    private Label statusBar;

    public MainController(GameSession session) {
        this.session = session;
    }

    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        show("/ui/DashboardView.fxml", DashboardController.class);
    }

    @FXML
    private void showRockets() {
        show("/ui/RocketsView.fxml", RocketsController.class);
    }

    @FXML
    private void showEmployees() {
        show("/ui/EmployeesView.fxml", EmployeesController.class);
    }

    @FXML
    private void showMissions() {
        show("/ui/MissionsView.fxml", MissionsController.class);
    }

    @FXML
    private void showFinance() {
        show("/ui/FinanceView.fxml", FinanceController.class);
    }

    @FXML
    private void showStatistics() {
        show("/ui/StatisticsView.fxml", StatisticsController.class);
    }

    private void show(String fxml, Class<?> controllerType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setControllerFactory(this::create);
            Parent view = loader.load();
            root.setCenter(view);
            Object controller = loader.getController();
            if (!controllerType.isInstance(controller)) {
                throw new IllegalStateException("Не тот контроллер: " + controller.getClass());
            }
            ((Refreshable) controller).refresh();
            refreshStatus();
        } catch (IOException | IllegalStateException e) {
            UiUtils.error("Экран не открылся: " + e.getMessage());
        }
    }

    /** Создаёт контроллеры с общей сессией (внедрение через конструктор). */
    private Object create(Class<?> type) {
        Runnable changed = this::refreshStatus;
        if (type == MainController.class) {
            return new MainController(session);
        }
        if (type == DashboardController.class) {
            return new DashboardController(session);
        }
        if (type == RocketsController.class) {
            return new RocketsController(session, changed);
        }
        if (type == EmployeesController.class) {
            return new EmployeesController(session, changed);
        }
        if (type == MissionsController.class) {
            return new MissionsController(session, changed);
        }
        if (type == FinanceController.class) {
            return new FinanceController(session, changed);
        }
        if (type == StatisticsController.class) {
            return new StatisticsController(session);
        }
        throw new IllegalArgumentException("Неизвестный контроллер: " + type);
    }

    /** Обновляет строку состояния (баланс). Вызывать из FX-потока. */
    public void refreshStatus() {
        statusBar.setText(session.company().getName() + "  •  " + session.finance().balanceSummary());
    }
}
