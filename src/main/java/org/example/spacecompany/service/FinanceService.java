package org.example.spacecompany.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.domain.finance.Transaction;
import org.example.spacecompany.domain.finance.TransactionType;
import org.example.spacecompany.util.MoneyUtils;

/**
 * Единая точка для всех движений денег.
 *
 * <p>Все пополнения/списания идут отсюда (или через
 * {@link Company#deposit}/{@link Company#withdraw}, которые сервис оборачивает),
 * поэтому история операций всегда полная. Статистика считается через Stream API,
 * а сам список живёт в {@link Company}.
 */
public class FinanceService {

    private final Company company;

    public FinanceService(Company company) {
        this.company = Objects.requireNonNull(company, "company");
    }

    public BigDecimal getBalance() {
        return company.getBalance();
    }

    public void deposit(BigDecimal amount, String description) {
        company.deposit(amount, description);
    }

    public void withdraw(BigDecimal amount, String description) {
        company.withdraw(amount, description);
    }

    public boolean canAfford(BigDecimal amount) {
        return company.canAfford(amount);
    }

    /** Full money history, newest last. */
    public List<Transaction> history() {
        return company.getTransactions();
    }

    /** Sum of all INCOME transactions. Streams example. */
    public BigDecimal totalIncome() {
        return company.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Sum of all EXPENSE transactions. Streams example. */
    public BigDecimal totalExpenses() {
        return company.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Income minus expenses. */
    public BigDecimal netProfit() {
        return totalIncome().subtract(totalExpenses());
    }

    /** Однострочная сводка баланса для нескольких меню. */
    public String balanceSummary() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Баланс: ").append(MoneyUtils.format(getBalance()));
        sb.append(" | доходы: ").append(MoneyUtils.format(totalIncome()));
        sb.append(" | расходы: ").append(MoneyUtils.format(totalExpenses()));
        sb.append(" | итого: ").append(MoneyUtils.format(netProfit()));
        return sb.toString();
    }
}
