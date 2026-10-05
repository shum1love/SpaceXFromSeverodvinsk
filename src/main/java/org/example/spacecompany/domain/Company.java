package org.example.spacecompany.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.example.spacecompany.domain.finance.Transaction;
import org.example.spacecompany.domain.finance.TransactionType;
import org.example.spacecompany.exception.InsufficientFundsException;
import org.example.spacecompany.repository.EmployeeRepository;
import org.example.spacecompany.repository.MissionRepository;
import org.example.spacecompany.repository.RocketRepository;

/**
 * Корень игры: космическая компания игрока.
 *
 * <p>Пример <b>композиции</b>: у компании <i>есть</i> склады ракет/людей/миссий
 * и денежная история — вместо наследования чего-либо.
 * Все зависимости приходят через конструктор (внедрение через конструктор),
 * класс сам ничего не создаёт и легко тестируется.
 *
 * <p>Деньги — {@link BigDecimal} с точностью до копеек. Напрямую баланс менять
 * снаружи нельзя: деньги ходят только через {@link #deposit} / {@link #withdraw},
 * каждая операция пишет {@link Transaction}.
 */
public class Company {

    private String name;
    private BigDecimal balance;
    private final LocalDate foundedDate;
    private final RocketRepository rockets;
    private final EmployeeRepository employees;
    private final MissionRepository missions;
    private final List<Transaction> transactions =
            Collections.synchronizedList(new ArrayList<>());
    private final List<LaunchRecord> launchHistory =
            Collections.synchronizedList(new ArrayList<>());

    public Company(String name, BigDecimal startingBalance,
                   RocketRepository rockets,
                   EmployeeRepository employees,
                   MissionRepository missions) {
        this(name, startingBalance, rockets, employees, missions, LocalDate.now());
    }

    /** Full constructor; the explicit date is used when loading a saved game. */
    public Company(String name, BigDecimal startingBalance,
                   RocketRepository rockets,
                   EmployeeRepository employees,
                   MissionRepository missions,
                   LocalDate foundedDate) {
        setName(name);
        if (startingBalance == null || startingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Стартовый баланс не может быть отрицательным");
        }
        this.balance = startingBalance.setScale(2, RoundingMode.HALF_EVEN);
        this.foundedDate = Objects.requireNonNull(foundedDate, "foundedDate");
        this.rockets = Objects.requireNonNull(rockets, "rockets");
        this.employees = Objects.requireNonNull(employees, "employees");
        this.missions = Objects.requireNonNull(missions, "missions");
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название компании не должно быть пустым");
        }
        this.name = name.strip();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDate getFoundedDate() {
        return foundedDate;
    }

    public RocketRepository getRockets() {
        return rockets;
    }

    public EmployeeRepository getEmployees() {
        return employees;
    }

    public MissionRepository getMissions() {
        return missions;
    }

    /** Unmodifiable view of the money history (newest last). */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /** Unmodifiable view of finished flights (newest last). */
    public List<LaunchRecord> getLaunchHistory() {
        return Collections.unmodifiableList(launchHistory);
    }

    /**
     * Adds money and records an INCOME transaction.
     * Synchronized: several missions may finish concurrently.
     */
    public synchronized void deposit(BigDecimal amount, String description) {
        requirePositiveAmount(amount);
        balance = balance.add(amount).setScale(2, RoundingMode.HALF_EVEN);
        transactions.add(new Transaction(TransactionType.INCOME, amount, description));
    }

    /**
     * Removes money and records an EXPENSE transaction.
     * Synchronized: several missions may be charged concurrently.
     *
     * @throws InsufficientFundsException when the balance is too low
     */
    public synchronized void withdraw(BigDecimal amount, String description) {
        requirePositiveAmount(amount);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Нужно %s, а на балансе только %s (%s)".formatted(amount, balance, description));
        }
        balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);
        transactions.add(new Transaction(TransactionType.EXPENSE, amount, description));
    }

    /** True when the balance covers the amount. */
    public boolean canAfford(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        return balance.compareTo(amount) >= 0;
    }

    public void addLaunchRecord(LaunchRecord record) {
        launchHistory.add(Objects.requireNonNull(record, "record"));
    }

    /** Restore hook for persistence (replaces money history). */
    public void restoreTransactions(List<Transaction> loaded) {
        transactions.clear();
        transactions.addAll(loaded);
    }

    /** Restore hook for persistence (replaces flight history). */
    public void restoreLaunchHistory(List<LaunchRecord> loaded) {
        launchHistory.clear();
        launchHistory.addAll(loaded);
    }

    /** Restore hook for persistence (used on load). */
    public void restoreBalance(BigDecimal loadedBalance) {
        this.balance = loadedBalance.setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Replaces the entire state of this company with the state of {@code other}.
     * Used by "Load game": repository and service references held by the rest
     * of the application stay valid because the repositories themselves are
     * refilled instead of swapped.
     */
    public void restoreFrom(Company other) {
        Objects.requireNonNull(other, "other");
        setName(other.getName());
        restoreBalance(other.getBalance());
        getRockets().clear();
        for (org.example.spacecompany.domain.Rocket rocket : other.getRockets().findAll()) {
            getRockets().save(rocket);
        }
        getEmployees().clear();
        for (org.example.spacecompany.domain.employee.Employee employee : other.getEmployees().findAll()) {
            getEmployees().save(employee);
        }
        getMissions().clear();
        for (org.example.spacecompany.domain.mission.Mission mission : other.getMissions().findAll()) {
            getMissions().save(mission);
        }
        restoreTransactions(new ArrayList<>(other.getTransactions()));
        restoreLaunchHistory(new ArrayList<>(other.getLaunchHistory()));
    }

    private static void requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть положительной, получено " + amount);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company other)) {
            return false;
        }
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "Компания{название='%s', баланс=%s, основана=%s, ракет=%d, сотрудников=%d, миссий=%d, полётов=%d}"
                .formatted(name, balance, foundedDate,
                        rockets.count(), employees.count(), missions.count(), launchHistory.size());
    }
}
