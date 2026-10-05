package org.example.spacecompany.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.example.spacecompany.GameSession;
import org.example.spacecompany.util.MoneyUtils;

/**
 * Экран «Статистика»: цифры + диаграмма миссий по статусам.
 * Всё считает {@code StatisticsService}; диаграмма — просто рисунок поверх
 * готового {@code Map<MissionStatus, Long>}.
 */
public class StatisticsController implements Refreshable {

    private final GameSession session;

    @FXML
    private Label flightsValue;

    @FXML
    private Label successValue;

    @FXML
    private Label moneyValue;

    @FXML
    private Label bestValue;

    @FXML
    private BarChart<String, Number> statusChart;

    @FXML
    private CategoryAxis statusAxis;

    @FXML
    private NumberAxis countAxis;

    public StatisticsController(GameSession session) {
        this.session = session;
    }

    @Override
    public void refresh() {
        var stats = session.statistics();
        flightsValue.setText(stats.totalFlights() + " (успешных " + stats.successfulFlights() + ")");
        successValue.setText(MoneyUtils.formatPercent(stats.successRate()));
        moneyValue.setText("Доходы " + MoneyUtils.format(stats.totalIncome())
                + " • расходы " + MoneyUtils.format(stats.totalExpenses()));
        bestValue.setText(stats.bestEmployee()
                .map(e -> e.getName() + " [" + EmployeesController.roleName(e.getRole()) + "]")
                .orElse("—") + "  •  " + stats.mostReliableRocket()
                .map(r -> r.getName() + " (" + "%.2f".formatted(r.getReliability()) + ")")
                .orElse("—"));

        statusChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Миссии");
        var grouped = new java.util.TreeMap<>(stats.missionsByStatus());
        for (var entry : grouped.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().name(), entry.getValue()));
        }
        statusChart.getData().add(series);
    }
}
