package org.example.spacecompany.domain.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Ставит эксперименты и разбирает данные. Ключевой для научных миссий. */
public class Scientist extends Employee {

    public Scientist(String name, BigDecimal salary, int experienceYears, int skillLevel) {
        super(name, salary, experienceYears, skillLevel);
    }

    public Scientist(UUID id, String name, BigDecimal salary,
                     int experienceYears, int skillLevel, LocalDate hireDate) {
        super(id, name, salary, experienceYears, skillLevel, hireDate);
    }

    @Override
    public EmployeeRole getRole() {
        return EmployeeRole.SCIENTIST;
    }

    @Override
    public String describeDuties() {
        return "Ставит эксперименты и анализирует научные данные миссий.";
    }

    @Override
    public double getMissionBonus() {
        return 0.02 + getSkillLevel() * 0.005;
    }

    @Override
    public String work() {
        return "Учёный " + getName() + " калибрует лабораторное оборудование.";
    }

    /** Estimated publication count derived from experience. */
    public int publications() {
        return getExperienceYears() * 2;
    }
}
