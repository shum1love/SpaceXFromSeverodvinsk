package org.example.spacecompany.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.spacecompany.GameSession;
import org.example.spacecompany.domain.finance.Transaction;
import org.example.spacecompany.domain.finance.TransactionType;
import org.example.spacecompany.util.MoneyUtils;

/**
 * Экран «Финансы»: сводка баланса + история операций + зарплаты.
 * Суммы считает {@code FinanceService} стримами; здесь только таблица.
 * Новые операции — сверху (история в компании хранится старыми сверху).
 */
public class FinanceController extends BaseViewController {

    @FXML
    private Label summaryLabel;

    @FXML
    private TableView<Transaction> table;

    @FXML
    private TableColumn<Transaction, String> timeColumn;

    @FXML
    private TableColumn<Transaction, String> amountColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    public FinanceController(GameSession session, Runnable changed) {
        super(session, changed);
    }

    @FXML
    public void initialize() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        amountColumn.getStyleClass().add("col-num");
        Cells.text(timeColumn, transaction -> transaction.getTimestamp().toString());
        Cells.coloredText(amountColumn,
                transaction -> {
                    String sign = transaction.getType() == TransactionType.INCOME ? "+" : "−";
                    return sign + MoneyUtils.format(transaction.getAmount());
                },
                transaction -> transaction.getType() == TransactionType.INCOME
                        ? javafx.scene.paint.Color.web("#3fce7a")
                        : javafx.scene.paint.Color.web("#f0564d"));
        Cells.text(descriptionColumn, Transaction::getDescription);
    }

    @Override
    public void refresh() {
        summaryLabel.setText(session.finance().balanceSummary());
        var reversed = new java.util.ArrayList<>(session.finance().history());
        java.util.Collections.reverse(reversed);
        table.setItems(FXCollections.observableArrayList(reversed));
    }

    @FXML
    private void paySalaries() {
        attempt(() -> session.employees().payMonthlySalaries(),
                "Зарплаты выплачены.", "Финансы");
    }
}
