package org.example.spacecompany.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.spacecompany.GameSession;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeStatus;
import org.example.spacecompany.util.MoneyUtils;

/**
 * Экран «Сотрудники»: таблица штата + найм/увольнение/обучение/отпуск/зарплаты.
 * Правила (кого можно уволить, потолок навыка) — в сервисах; здесь вызовы
 * через {@code attempt} и цветные пилюли статусов.
 */
public class EmployeesController extends BaseViewController {

    @FXML
    private TableView<Employee> table;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private TableColumn<Employee, String> roleColumn;

    @FXML
    private TableColumn<Employee, String> salaryColumn;

    @FXML
    private TableColumn<Employee, String> experienceColumn;

    @FXML
    private TableColumn<Employee, String> skillColumn;

    @FXML
    private TableColumn<Employee, String> statusColumn;

    public EmployeesController(GameSession session, Runnable changed) {
        super(session, changed);
    }

    @FXML
    public void initialize() {
        Cells.text(nameColumn, Employee::getName);
        Cells.text(roleColumn, employee -> Cells.roleName(employee.getRole()));
        Cells.text(salaryColumn, employee -> MoneyUtils.format(employee.getSalary()));
        Cells.text(experienceColumn, employee -> employee.getExperienceYears() + " л.");
        Cells.text(skillColumn, employee -> String.valueOf(employee.getSkillLevel()));
        Cells.pill(statusColumn,
                employee -> Cells.employeeName(employee.getStatus()),
                employee -> Cells.employeePill(employee.getStatus()));
    }

    @Override
    public void refresh() {
        table.setItems(FXCollections.observableArrayList(session.employees().findAll()));
    }

    @FXML
    private void hire() {
        Dialogs.hireEmployee().ifPresent(employee ->
                attempt(() -> session.employees().hire(employee),
                        "Нанят(а): " + employee.getName(), "Сотрудники"));
    }

    @FXML
    private void fire() {
        Employee employee = requireSelection(table, "сотрудника");
        if (employee == null) {
            return;
        }
        if (!UiUtils.confirm("Уволить " + employee.getName() + "?")) {
            return;
        }
        attempt(() -> session.employees().fire(employee.getId()),
                employee.getName() + " уволен(а).", "Сотрудники");
    }

    @FXML
    private void train() {
        Employee employee = requireSelection(table, "сотрудника");
        if (employee == null) {
            return;
        }
        attempt(() -> session.employees().train(employee.getId()),
                employee.getName() + " обучен(а), навык: " + employee.getSkillLevel(), "Сотрудники");
    }

    @FXML
    private void toggleLeave() {
        Employee employee = requireSelection(table, "сотрудника");
        if (employee == null) {
            return;
        }
        if (employee.getStatus() == EmployeeStatus.ON_LEAVE) {
            attempt(() -> session.employees().returnFromLeave(employee.getId()),
                    employee.getName() + " вернулся из отпуска.", "Сотрудники");
        } else {
            attempt(() -> session.employees().sendOnLeave(employee.getId()),
                    employee.getName() + " ушёл в отпуск.", "Сотрудники");
        }
    }

    @FXML
    private void paySalaries() {
        attempt(() -> session.employees().payMonthlySalaries(),
                "Зарплаты выплачены.", "Сотрудники");
    }

    /**
     * Русское имя роли. Оставлено для совместимости: используют экраны
     * миссий и статистики. Новому коду — сразу {@code Cells.roleName}.
     */
    static String roleName(org.example.spacecompany.domain.employee.EmployeeRole role) {
        return Cells.roleName(role);
    }
}
