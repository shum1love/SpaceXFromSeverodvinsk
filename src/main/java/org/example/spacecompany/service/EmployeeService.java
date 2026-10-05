package org.example.spacecompany.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.example.spacecompany.domain.employee.Employee;
import org.example.spacecompany.domain.employee.EmployeeRole;
import org.example.spacecompany.domain.employee.EmployeeStatus;
import org.example.spacecompany.exception.EmployeeNotAvailableException;
import org.example.spacecompany.repository.EmployeeRepository;

/**
 * Найм, увольнение, зарплаты и поиск экипажа.
 */
public class EmployeeService {

    private final EmployeeRepository employees;
    private final FinanceService finance;

    public EmployeeService(EmployeeRepository employees, FinanceService finance) {
        this.employees = Objects.requireNonNull(employees, "employees");
        this.finance = Objects.requireNonNull(finance, "finance");
    }

    /** Hires an employee (no signing fee in this version, just records them). */
    public Employee hire(Employee employee) {
        Objects.requireNonNull(employee, "employee");
        employee.setStatus(EmployeeStatus.AVAILABLE);
        employees.save(employee);
        return employee;
    }

    /**
     * Fires an employee. People on a mission cannot be fired mid-flight.
     *
     * @return true when someone was actually removed
     */
    public boolean fire(UUID employeeId) {
        Employee employee = employees.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotAvailableException("Сотрудник не найден: " + employeeId));
        if (employee.getStatus() == EmployeeStatus.ON_MISSION) {
            throw new EmployeeNotAvailableException(
                    "Нельзя уволить " + employee.getName() + " прямо на миссии");
        }
        return employees.deleteById(employeeId);
    }

    /** Pays one monthly salary to every employee. Loops example (plain for). */
    public BigDecimal payMonthlySalaries() {
        List<Employee> all = employees.findAll();
        BigDecimal total = BigDecimal.ZERO;
        for (Employee employee : all) {
            total = total.add(employee.getSalary());
        }
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            finance.withdraw(total, "Зарплаты за месяц: " + all.size() + " сотр.");
        }
        return total;
    }

    /** Trains an employee (+1 skill), demonstrating delegation to the domain. */
    public void train(UUID employeeId) {
        Employee employee = require(employeeId);
        employee.train();
    }

    public void sendOnLeave(UUID employeeId) {
        Employee employee = require(employeeId);
        if (employee.getStatus() == EmployeeStatus.ON_MISSION) {
            throw new EmployeeNotAvailableException(
                    employee.getName() + " на миссии и не может уйти в отпуск прямо сейчас");
        }
        employee.setStatus(EmployeeStatus.ON_LEAVE);
    }

    public void returnFromLeave(UUID employeeId) {
        Employee employee = require(employeeId);
        if (employee.getStatus() == EmployeeStatus.ON_LEAVE) {
            employee.setStatus(EmployeeStatus.AVAILABLE);
        }
    }

    public Optional<Employee> findById(UUID id) {
        return employees.findById(id);
    }

    public List<Employee> findAll() {
        return employees.findAll();
    }

    /** Available employees of one role. Stream + lambda example. */
    public List<Employee> findAvailableByRole(EmployeeRole role) {
        return employees.findAll().stream()
                .filter(e -> e.getRole() == role)
                .filter(Employee::isAvailable)
                .toList();
    }

    /** All available pilots, sorted by skill (best first). Method reference sort. */
    public List<Employee> findAvailablePilots() {
        return findAvailableByRole(EmployeeRole.PILOT).stream()
                .sorted(Comparator.comparingInt(Employee::getSkillLevel).reversed())
                .toList();
    }

    /** Highest (skill, then experience) employee, if anyone is hired. */
    public Optional<Employee> findBestEmployee() {
        return employees.findAll().stream()
                .max(Comparator.comparingInt(Employee::getSkillLevel)
                        .thenComparingInt(Employee::getExperienceYears));
    }

    private Employee require(UUID employeeId) {
        return employees.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotAvailableException("Сотрудник не найден: " + employeeId));
    }
}
