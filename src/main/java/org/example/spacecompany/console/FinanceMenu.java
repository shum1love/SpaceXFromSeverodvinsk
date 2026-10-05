package org.example.spacecompany.console;

import java.util.List;
import org.example.spacecompany.domain.LaunchRecord;
import org.example.spacecompany.domain.finance.Transaction;
import org.example.spacecompany.service.EmployeeService;
import org.example.spacecompany.service.FinanceService;
import org.example.spacecompany.util.InputReader;
import org.example.spacecompany.util.MoneyUtils;

/** Подменю «Финансы»: баланс, история операций, зарплаты. */
public class FinanceMenu {

    private static final int HISTORY_PAGE = 15;

    private final FinanceService finance;
    private final EmployeeService employees;
    private final InputReader input;

    public FinanceMenu(FinanceService finance, EmployeeService employees, InputReader input) {
        this.finance = finance;
        this.employees = employees;
        this.input = input;
    }

    public void show() {
        boolean back = false;
        while (!back) {
            MenuIO.printHeader("Финансы");
            MenuIO.info(finance.balanceSummary());
            System.out.println("1. История операций");
            System.out.println("2. Выплатить зарплаты сейчас");
            System.out.println("0. Назад");
            int choice = input.readInt("Выбор: ", 0, 2);
            switch (choice) {
                case 1 -> showHistory();
                case 2 -> employees.payMonthlySalaries();
                case 0 -> back = true;
                default -> System.out.println("Неизвестный пункт.");
            }
            if (choice == 2) {
                MenuIO.ok("Зарплаты выплачены. " + finance.balanceSummary());
            }
        }
    }

    private void showHistory() {
        List<Transaction> history = finance.history();
        if (history.isEmpty()) {
            MenuIO.info("Операций пока нет.");
            return;
        }
        MenuIO.info("Последние " + Math.min(HISTORY_PAGE, history.size())
                + " из " + history.size() + " операций (старые сверху):");
        int from = Math.max(0, history.size() - HISTORY_PAGE);
        for (int i = from; i < history.size(); i++) {
            Transaction t = history.get(i);
            String sign = switch (t.getType()) {
                case INCOME -> "+";
                case EXPENSE -> "-";
            };
            MenuIO.info("  " + t.getTimestamp() + "  " + sign + MoneyUtils.format(t.getAmount())
                    + "  " + t.getDescription());
        }
        MenuIO.info("Прибыль за всё время: " + MoneyUtils.format(finance.netProfit()));
    }

    /** Печатает свежие полёты; используется экраном обзора. */
    public void showLaunchHistory(List<LaunchRecord> records) {
        if (records.isEmpty()) {
            MenuIO.info("Полётов пока не было.");
            return;
        }
        int from = Math.max(0, records.size() - HISTORY_PAGE);
        for (int i = from; i < records.size(); i++) {
            MenuIO.info("  " + records.get(i));
        }
    }
}
