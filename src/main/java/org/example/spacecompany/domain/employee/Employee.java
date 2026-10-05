package org.example.spacecompany.domain.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * База для всех сотрудников компании.
 *
 * <p>Наследники ({@link Engineer}, {@link Pilot}, {@link Scientist},
 * {@link MissionSpecialist}) различаются ролью, бонусом миссии и характером.
 * Общее (id, имя, зарплата, опыт, навык, статус, дата найма) и общее поведение
 * (обучение, рост опыта) живут здесь.
 *
 * <p>Равенство — по уникальному {@code id}: объекты с одним id — один человек.
 */
public abstract class Employee {

    /** Minimum/maximum skill level. */
    public static final int MIN_SKILL = 1;
    public static final int MAX_SKILL = 10;

    private final UUID id;
    private String name;
    private BigDecimal salary;
    private int experienceYears;
    private int skillLevel;
    private EmployeeStatus status;
    private final LocalDate hireDate;

    protected Employee(UUID id, String name, BigDecimal salary,
                       int experienceYears, int skillLevel, LocalDate hireDate) {
        this.id = Objects.requireNonNull(id, "id");
        setName(name);
        setSalary(salary);
        setExperienceYears(experienceYears);
        setSkillLevel(skillLevel);
        this.status = EmployeeStatus.AVAILABLE;
        this.hireDate = Objects.requireNonNull(hireDate, "hireDate");
    }

    protected Employee(String name, BigDecimal salary, int experienceYears, int skillLevel) {
        this(UUID.randomUUID(), name, salary, experienceYears, skillLevel, LocalDate.now());
    }

    /** Role of this employee, used when validating mission crews. */
    public abstract EmployeeRole getRole();

    /** One-line human-readable description of what this employee does. */
    public abstract String describeDuties();

    /**
     * Extra success probability (e.g. 0.05 = +5 percentage points) this
     * employee contributes to a mission. Depends on the concrete profession.
     */
    public abstract double getMissionBonus();

    /**
     * Чем сотрудник занят прямо сейчас. Наследники переопределяют ради колорита
     * (пример переопределения методов).
     */
    public String work() {
        return getRole() + " " + name + " работает.";
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя сотрудника не должно быть пустым");
        }
        this.name = name.strip();
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public final void setSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Зарплата не может быть отрицательной");
        }
        this.salary = salary;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public final void setExperienceYears(int experienceYears) {
        if (experienceYears < 0) {
            throw new IllegalArgumentException("Опыт не может быть отрицательным");
        }
        this.experienceYears = experienceYears;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public final void setSkillLevel(int skillLevel) {
        if (skillLevel < MIN_SKILL || skillLevel > MAX_SKILL) {
            throw new IllegalArgumentException(
                    "Навык должен быть в пределах " + MIN_SKILL + ".." + MAX_SKILL + ", получено " + skillLevel);
        }
        this.skillLevel = skillLevel;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public boolean isAvailable() {
        return status == EmployeeStatus.AVAILABLE;
    }

    /** Raises skill by one point, capped at {@link #MAX_SKILL}. Overloaded. */
    public void train() {
        train(1);
    }

    /**
     * Raises skill by the given number of points, capped at
     * {@link #MAX_SKILL}. Overloaded: see {@link #train()}.
     */
    public void train(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Очки обучения должны быть положительными");
        }
        setSkillLevel(Math.min(MAX_SKILL, skillLevel + points));
    }

    /** Adds one year of experience (called after a completed mission). */
    public void gainExperience() {
        experienceYears++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "%s{id=%s, имя='%s', зарплата=%s, опыт=%d л., навык=%d, статус=%s}"
                .formatted(getClass().getSimpleName(), id.toString().substring(0, 8),
                        name, salary, experienceYears, skillLevel, status);
    }
}
