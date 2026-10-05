package org.example.spacecompany.domain.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Чинит и обслуживает ракеты. Поднимает надёжность миссий. */
public class Engineer extends Employee {

    public Engineer(String name, BigDecimal salary, int experienceYears, int skillLevel) {
        super(name, salary, experienceYears, skillLevel);
    }

    public Engineer(UUID id, String name, BigDecimal salary,
                    int experienceYears, int skillLevel, LocalDate hireDate) {
        super(id, name, salary, experienceYears, skillLevel, hireDate);
    }

    @Override
    public EmployeeRole getRole() {
        return EmployeeRole.ENGINEER;
    }

    @Override
    public String describeDuties() {
        return "Обслуживает ракеты и чинит технику до и после полётов.";
    }

    @Override
    public double getMissionBonus() {
        return 0.02 + getSkillLevel() * 0.005;
    }

    @Override
    public String work() {
        return "Инженер " + getName() + " осматривает двигатели.";
    }

    /** Repair power used when estimating post-flight maintenance. */
    public int repairPower() {
        return getSkillLevel() * 2 + getExperienceYears();
    }
}
