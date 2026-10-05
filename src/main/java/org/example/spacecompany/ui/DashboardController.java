package org.example.spacecompany.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.spacecompany.GameSession;
import org.example.spacecompany.domain.LaunchRecord;
import org.example.spacecompany.domain.finance.Transaction;
import org.example.spacecompany.util.MoneyUtils;

/**
 * Экран «Обзор»: ключевые цифры компании + свежие полёты и операции.
 * Только читает сервисы и рисует. Обновляется при каждом показе.
 */
public class DashboardController implements Refreshable {

    private final GameSession session;

    @FXML
    private Label companyName;

    @FXML
    private Label foundedLabel;

    @FXML
    private Label balanceValue;

    @FXML
    private Label rocketsValue;

    @FXML
    private Label employeesValue;

    @FXML
    private Label missionsValue;

    @FXML
    private Label successValue;

    @FXML
    private ListView<String> flightsList;

    @FXML
    private ListView<String> moneyList;

    public DashboardController(GameSession session) {
        this.session = session;
    }

    @Override
    public void refresh() {
        companyName.setText("🚀 " + session.company().getName());
        foundedLabel.setText("основана " + session.company().getFoundedDate()
                + " • через тернии — к звёздам");
        balanceValue.setText(MoneyUtils.format(session.finance().getBalance()));
        rocketsValue.setText(String.valueOf(session.company().getRockets().count()));
        employeesValue.setText(String.valueOf(session.company().getEmployees().count()));
        missionsValue.setText(String.valueOf(session.company().getMissions().count()));
        successValue.setText(MoneyUtils.formatPercent(session.statistics().successRate())
                + " (" + session.statistics().successfulFlights()
                + "/" + session.statistics().totalFlights() + ")");

        flightsList.getItems().clear();
        var history = session.company().getLaunchHistory();
        for (int i = history.size() - 1; i >= 0 && flightsList.getItems().size() < 8; i--) {
            LaunchRecord record = history.get(i);
            flightsList.getItems().add((record.isSuccess() ? "✅ " : "❌ ") + record);
        }
        if (flightsList.getItems().isEmpty()) {
            flightsList.getItems().add("Полётов пока не было — соберите миссию!");
        }

        moneyList.getItems().clear();
        var transactions = session.finance().history();
        for (int i = transactions.size() - 1; i >= 0 && moneyList.getItems().size() < 8; i--) {
            Transaction t = transactions.get(i);
            String sign = t.getType() == org.example.spacecompany.domain.finance.TransactionType.INCOME ? "+" : "−";
            moneyList.getItems().add(sign + MoneyUtils.format(t.getAmount()) + " — " + t.getDescription());
        }
    }
}
