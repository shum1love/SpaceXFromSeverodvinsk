package org.example.spacecompany.domain.employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Универсальный член экипажа. Джокер при проверке требований: специалист
 * закрывает любую одну недостающую роль.
 */
public class MissionSpecialist extends Employee {

    public MissionSpecialist(String name, BigDecimal salary, int experienceYears, int skillLevel) {
        super(name, salary, experienceYears, skillLevel);
    }

    public MissionSpecialist(UUID id, String name, BigDecimal salary,
                             int experienceYears, int skillLevel, LocalDate hireDate) {
        super(id, name, salary, experienceYears, skillLevel, hireDate);
    }

    @Override
    public EmployeeRole getRole() {
        return EmployeeRole.MISSION_SPECIALIST;
    }

    @Override
    public String describeDuties() {
        return "Универсал: помогает с пилотированием, инженерией и наукой.";
    }

    @Override
    public double getMissionBonus() {
        return 0.015 + getSkillLevel() * 0.004;
    }

    @Override
    public String work() {
        return "Специалист " + getName() + " готовится к следующему полёту по всем направлениям.";
    }
}
