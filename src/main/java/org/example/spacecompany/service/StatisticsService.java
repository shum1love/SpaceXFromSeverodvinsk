package org.example.spacecompany.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import org.example.spacecompany.domain.Company;
import org.example.spacecompany.domain.LaunchRecord;
import org.example.spacecompany.domain.Rocket;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.mission.Mission;
import org.example.spacecompany.domain.mission.MissionStatus;

/**
 * Аналитика только для чтения по состоянию компании.
 *
 * <p>Сознательно смешивает обычные циклы и Stream API: простой подсчёт часто
 * яснее циклом, а группировка/сортировка/агрегация расцветают стримами.
 * Оба стиля живут в реальном коде, оба спрашивают на собеседованиях.
 */
public class StatisticsService {

    private final Company company;
    private final FinanceService finance;

    public StatisticsService(Company company, FinanceService finance) {
        this.company = Objects.requireNonNull(company, "company");
        this.finance = Objects.requireNonNull(finance, "finance");
    }

    /** Share of successful flights, 0..1. Plain-loop example. */
    public double successRate() {
        List<LaunchRecord> history = company.getLaunchHistory();
        if (history.isEmpty()) {
            return 0.0;
        }
        int wins = 0;
        for (LaunchRecord record : history) {
            if (record.isSuccess()) {
                wins++;
            }
        }
        return (double) wins / history.size();
    }

    public long totalFlights() {
        return company.getLaunchHistory().size();
    }

    public long successfulFlights() {
        long count = 0;
        for (LaunchRecord record : company.getLaunchHistory()) {
            if (record.isSuccess()) {
                count++;
            }
        }
        return count;
    }

    /** Missions grouped by status. groupingBy collector example. */
    public Map<MissionStatus, Long> missionsByStatus() {
        return company.getMissions().findAll().stream()
                .collect(Collectors.groupingBy(Mission::getStatus, Collectors.counting()));
    }

    /** The most reliable rocket owned, if any. max() + Optional example. */
    public Optional<Rocket> mostReliableRocket() {
        return company.getRockets().findAll().stream()
                .max(Comparator.comparingDouble(Rocket::getReliability));
    }

    /** The most flown rocket, if any. */
    public Optional<Rocket> mostFlownRocket() {
        return company.getRockets().findAll().stream()
                .max(Comparator.comparingInt(Rocket::getLaunchCount));
    }

    /** The most skilled employee (ties broken by experience). */
    public Optional<Employee> bestEmployee() {
        return company.getEmployees().findAll().stream()
                .max(Comparator.comparingInt(Employee::getSkillLevel)
                        .thenComparingInt(Employee::getExperienceYears));
    }

    /** Top-N earners by salary. sorted + limit example. */
    public List<Employee> topEarners(int n) {
        return company.getEmployees().findAll().stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(n)
                .toList();
    }

    /** Average mission reward on offer. Average over BigDecimals via double. */
    public OptionalDouble averageMissionReward() {
        return company.getMissions().findAll().stream()
                .mapToDouble(m -> m.getReward().doubleValue())
                .average();
    }

    public BigDecimal totalIncome() {
        return finance.totalIncome();
    }

    public BigDecimal totalExpenses() {
        return finance.totalExpenses();
    }

    /** Многострочный читаемый отчёт. Пример StringBuilder. */
    public String buildReport() {
        StringBuilder sb = new StringBuilder(512);
        sb.append("--- Статистика компании ---\n");
        sb.append("Полётов: ").append(totalFlights())
                .append(" (успешных ").append(successfulFlights())
                .append(", доля ").append("%.1f%%".formatted(successRate() * 100.0)).append(")\n");
        sb.append("Миссии по статусам: ").append(missionsByStatus()).append('\n');
        sb.append("Доходы: ").append(finance.totalIncome())
                .append(", расходы: ").append(finance.totalExpenses())
                .append(", итого: ").append(finance.netProfit()).append('\n');
        mostReliableRocket().ifPresent(r -> sb.append("Самая надёжная ракета: ")
                .append(r.getName()).append(" (")
                .append("%.2f".formatted(r.getReliability())).append(")\n"));
        mostFlownRocket().ifPresent(r -> sb.append("Самая летающая ракета: ")
                .append(r.getName()).append(" (").append(r.getLaunchCount()).append(" полётов)\n"));
        bestEmployee().ifPresent(e -> sb.append("Лучший сотрудник: ")
                .append(e.getName()).append(" [").append(e.getRole())
                .append(", навык ").append(e.getSkillLevel()).append("]\n"));
        averageMissionReward().ifPresent(avg -> sb.append("Средняя награда за миссию: ")
                .append("%.2f".formatted(avg)).append('\n'));
        return sb.toString();
    }
}
