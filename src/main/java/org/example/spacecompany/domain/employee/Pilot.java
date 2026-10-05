package org.example.spacecompany.domain.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Пилотирует ракету. Нужен почти каждой миссии. */
public class Pilot extends Employee {

    public Pilot(String name, BigDecimal salary, int experienceYears, int skillLevel) {
        super(name, salary, experienceYears, skillLevel);
    }

    public Pilot(UUID id, String name, BigDecimal salary,
                 int experienceYears, int skillLevel, LocalDate hireDate) {
        super(id, name, salary, experienceYears, skillLevel, hireDate);
    }

    @Override
    public EmployeeRole getRole() {
        return EmployeeRole.PILOT;
    }

    @Override
    public String describeDuties() {
        return "Пилотирует ракету и отвечает за безопасность экипажа в полёте.";
    }

    @Override
    public double getMissionBonus() {
        return 0.03 + getSkillLevel() * 0.006;
    }

    @Override
    public String work() {
        return "Пилот " + getName() + " проводит предполётные проверки.";
    }

    /** Estimated flight hours derived from experience. */
    public int flightHours() {
        return getExperienceYears() * 120 + getSkillLevel() * 10;
    }
}
